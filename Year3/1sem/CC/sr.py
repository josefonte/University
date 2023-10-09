import socket, threading, time, re, random
from cache import cache, cacheEntry, origin, state
from config import config
from rwlock import ReadWriteLock
from query import query
from logs import log,LogFile
from bitarray import bitarray


#python3 server.py ./config_files/SR.conf

class serverSR():

    def __init__(self,server_config : config, port=7000, timeout=0, ipHost = "",debug = True):
        LogInit = log(time.time(),"ST", "127.0.0.1", f'{port} {timeout} Debug={debug}')
        self.config = server_config
        LogConfig = log(time.time(),"EV", "localhost", f'conf-file-read')
        self.timeout = timeout
        self.ipHost = ipHost 
        self.server_cache = cache(0)
        self.Logger = LogFile(self.config.lg, self.config.all_lg, debug)
        
        self.socketUDP = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.socketUDP.bind((ipHost, port))
        self.lockUDPsend = threading.RLock() 
        
        self.root_servers = dict()
        with open(self.config.root_st) as root_file:
            lines = root_file.readlines()

        for line in lines:
            args = line.split(' ')
            self.root_servers[args[1]] = 0

        LogRootServers = log(time.time(),"EV", "localhost", f'rootserver-file-read')

        self.request_pool = list()
        self.response_pool = dict() 
        self.rwlock = ReadWriteLock()
        self.event = threading.Event()

        self.Logger.write(LogInit)
        self.Logger.write(LogConfig)
        self.Logger.write(LogRootServers)

    def start(self):

        while True:
            data, client_address = self.socketUDP.recvfrom(4096)
            
            time.sleep(0.1)
            msg  = query(0,"", 0,0,0,0,"","", [],[],[])
            flag = True
            try:
                msg.decode(data)
            except:
                self.Logger.write(log(time.time(),"ER", "localhost", f'{msg}'))
                response = query(0,"",3,0,0,0,"","",[],[],[])
                flag = False
                with self.lockUDPsend:
                    self.socketUDP.sendto(response.encode(),client_address)

                self.Logger.write(log(time.time(),"RE", f'{client_address[0]}', f'{response}'))
            

            if flag:
                if  msg.messageID in self.request_pool and msg.flags == "A+R" or msg.flags == "A" or msg.flags=="":
                    self.Logger.write(log(time.time(),"RR", f'{client_address}', f'{msg}'))
                    self.rwlock.writerLock()
                    self.response_pool[msg.messageID] = msg
                    self.rwlock.writerUnlock()
                    self.event.set()


                elif msg.messageID not in self.request_pool:
                    self.Logger.write(log(time.time(),"QR", f'{client_address}', f'{msg}'))
                    self.rwlock.writerLock()
                    self.request_pool.append(msg.messageID)
                    self.rwlock.writerUnlock() 
                    t = threading.Thread(target=self.process_request, args=(msg, client_address))
                    t.start()
            elif msg.responseCode == 3 :
                response = query(0,"",3,0,0,0,"","",[],[],[])
                with self.lockUDPsend:
                    self.socketUDP.sendto(response.encode(),client_address)
                self.Logger.write(log(time.time(),"RE", f'{client_address[0]}', f'{response}'))

            else: 
                response = query(0,"",3,0,0,0,"","",[],[],[])
                with self.lockUDPsend:
                    self.socketUDP.sendto(response.encode(),client_address)
                self.Logger.write(log(time.time(),"RE", f'{client_address[0]}', f'{response}'))

    def process_request(self,request:query,client_address):
        

        response = self.getResponse(request)

        if response.noValues == 0 or response.noAuthorities == 0 or response.noExtraValues == 0:
            
            if request.flags == "Q":
                response = self.iterative_process(request)   

            elif request.flags == "Q+R" :
                response = self.recursive_process(request)
	
            response.flags = ""

            for line in response.responseValues:
                args = line.split(" ")
                newEntrie = cacheEntry(args[0],args[1],args[2], args[3],args[4],origin.OTHERS, 0, state.VALID)
                self.server_cache.register(newEntrie)


            for line in response.authValues:
                args = line.split(" ")
                newEntrie = cacheEntry(args[0],args[1],args[2], args[3],args[4],origin.OTHERS, 0, state.VALID)
                self.server_cache.register(newEntrie)

            for line in response.extraValues:
                args = line.split(" ")
                newEntrie = cacheEntry(args[0],args[1],args[2], args[3],args[4],origin.OTHERS, 0, state.VALID)
                self.server_cache.register(newEntrie)
            
        
        with self.lockUDPsend:
             self.socketUDP.sendto(response.encode(),client_address)
        self.Logger.write(log(time.time(),"RE", f"{client_address[0]}", f'{response}'))
   
        self.request_pool.remove(request.messageID)

        return


    def iterative_process(self,request : query): 

        domains= request.qi_name.split(".")
        if(len(domains) in (3,2)) :
            dominio = domains[0] + "." 
        else: 
            return  query(request.messageID,"", 2,0,0,0, request.qi_name, request.qi_typeValue , [],[],[])

        if( dominio == self.config.dominio): 
            sp_ip = (self.config.dd, 9000)
            with self.lockUDPsend :
                self.socketUDP.sendto(request.encode(), sp_ip)
            self.Logger.write(log(time.time(),"QE", f'{sp_ip}', f'{request}'))
            
            while(True):
                self.event.wait()
                if(self.in_responsepoll(request.messageID)) : 
                    self.event.clear()
                    break
            
            self.rwlock.writerLock()
            sp_response = self.response_pool.pop(request.messageID)
            self.rwlock.writerUnlock()

            return sp_response

            
        else:   
            st_domain = domains[-2] + "."
            st_request  = request
            
            #core #
            st_ip = ('10.0.13.10', 10000)
            #st_ip = ('127.0.0.1', 10000)

            with self.lockUDPsend :
                self.socketUDP.sendto(st_request.encode(), st_ip)

            self.Logger.write(log(time.time(),"QE1", f'{st_ip}', f'{st_request}'))

            while(True):
                self.event.wait()
                if(self.in_responsepoll(request.messageID)) : 
                    self.event.clear()
                    break
            
            self.rwlock.writerLock()
            st_response = self.response_pool.pop(request.messageID)
            self.rwlock.writerUnlock()

            servers_SDT = self.process_st_response(st_response)
            
            sdt_request = query(request.messageID,"Q", 0,0,0,0, request.qi_name, request.qi_typeValue, [],[],[])
            
            
            #core 
            sdt_ip = (servers_SDT["ns1"][0], 9000)
            
            #sdt_ip = ('127.0.0.1', 9000)
            
            with self.lockUDPsend :
                self.socketUDP.sendto(sdt_request.encode(), sdt_ip)

            self.Logger.write(log(time.time(),"QE2", f'{sdt_ip}', f'{sdt_request}'))

            while(True):
                self.event.wait()
                if(self.in_responsepoll(request.messageID)) :
                    self.event.clear()
                    break

            self.rwlock.writerLock()
            sdt_response = self.response_pool.pop(request.messageID)
            self.rwlock.writerUnlock()
            
            
            
            if sdt_response.flags == "A":  #significa que é resposta final
                return sdt_response
            
            else:

                sp_request = query(request.messageID,"Q", 0,0,0,0, request.qi_name, request.qi_typeValue, [],[],[])
                server_SP = self.process_sdt_response(sdt_response)

                #core #
                sp_ip = (server_SP["sp"][0], 9000)
                #sp_ip = ('127.0.0.1', 9050)
               
                with self.lockUDPsend :
                    self.socketUDP.sendto(sp_request.encode(), sp_ip)
                self.Logger.write(log(time.time(),"QE3", f'{sp_ip}', f'{sp_request}'))

                while(True):
                    self.event.wait()
                    if(self.in_responsepoll(request.messageID)) : 
                        self.event.clear()
                        break

                self.rwlock.writerLock()
                sp_response = self.response_pool.pop(request.messageID)
                self.rwlock.writerUnlock()
              
                return sp_response
            
            
    def recursive_process(self,request : query):
        
        domains= request.qi_name.split(".")
        if(len(domains) in (3,2)) :
            dominio = domains[0] + "." 
        else: 
            return  query(request.messageID,"", 2,0,0,0, request.qi_name, request.qi_typeValue , [],[],[])


        st_domain = domains[-2] + "."
        st_request  = query(request.messageID,"Q+R", 0,0,0,0, request.qi_name, request.qi_typeValue, [],[],[])
        
        #core #
        st_ip = ('10.0.13.10', 10000)
        #st_ip = ('127.0.0.1', 10000)
        

        with self.lockUDPsend :
            self.socketUDP.sendto(st_request.encode(), st_ip)
        self.Logger.write(log(time.time(),"QE", f'{st_ip}', f'{st_request}'))

        while(True):
            self.event.wait()
            if(self.in_responsepoll(request.messageID)) : 
                self.event.clear()
                break
        
        self.rwlock.writerLock()
        st_response = self.response_pool.pop(request.messageID)
        self.rwlock.writerUnlock()

        return st_response   



    ######################### AUXILIARES ###############################

    def process_st_response(self,response : query):
        
        servers = dict()

        for line in response.extraValues:
            args = line.split(" ")
            servers[args[0]] = (args[2], args[3])
        
        return servers 
    
    def process_sdt_response(self,response : query):
        
        servers = dict()

        for line in response.extraValues:
            args = line.split(" ")
            servers[args[0]] = (args[2], args[3])
        
        return servers 


    def root_server_less_congested (self):
        st, num = ('', 0)
        for root in self.root_servers.items() :
            if root[1] <= num :
                st = root[0]
                num = root[1]
        
        num+=1
        return st

    def in_responsepoll(self, msgID):
        self.rwlock.readLock()
        cond = msgID in self.response_pool.keys() 
        self.rwlock.readUnlock()

        return cond

    
    def getResponse(self, request : query):

        response = query(request.messageID,"",0,0,0,0,request.qi_name,request.qi_typeValue,[],[],[])
        if len(self.server_cache.entries) == 0:
            return response
        responseV = []
        authV = []
        extraV = []
        extraValues = set()
        responseList = self.server_cache.search((request.qi_name,request.qi_typeValue),0)
        for respElem in responseList:
            extraValues.add(respElem.value.replace(request.qi_name, ""))
            responseV.append(respElem.format())
        response.noValues = len(responseV)
        if len(responseV) == 0 : 
            response.responseCode = 1
        response.responseValues = responseV
        authList = self.server_cache.search((request.qi_name,"NS"),0)
        for authElem in authList:
            extraValues.add(authElem.value.replace(request.qi_name, ""))
            authV.append(authElem.format())
        response.noAuthorities = len(authV)
        if len(authV) == 0 : 
            response.responseCode = 2
        response.authValues = authV
        for a in extraValues:
            a= a[:-1]
            newExtraV = self.server_cache.search((a,"A"),0)
            if len(newExtraV) != 0 : 
                extraV.append(newExtraV[0].format())
        response.noExtraValues = len(extraV)
        response.extraValues = extraV
        
        return response  
