import os, socket, re, random, time,threading
from cache import cache,origin
from config import config
from query import query
from logs import log, LogFile
from rwlock import ReadWriteLock


BUFFERSIZE = 2048

#PORTA leitura UDP do SS = 8000 

#porta de escrita SP = 9000
#porta de escrita SR = 7000

class serverSS():

    def __init__(self,server_config : config, port=8000, timeout=0, ipHost = "",debug = True):
        LogInit = log(time.time(),"ST", "127.0.0.1", f'{port} {timeout} Debug={debug}')
        self.config = server_config
        LogConfig = log(time.time(),"EV", "localhost", f'conf-file-read')
        self.ipHost = ipHost
        self.port = port
        self.timeout = timeout
        self.server_cache = cache(0)
        self.Logger = LogFile(self.config.lg, self.config.all_lg, debug)
        self.socketUDP = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.lockUDP = threading.RLock() 
        self.lockTCP = threading.RLock()  

        self.socketUDP.bind((self.ipHost, self.port))

        self.request_pool = list()
        self.response_pool = dict() 
        self.rwlock = ReadWriteLock()
        self.event = threading.Event()

        self.Logger.write(LogInit)
        self.Logger.write(LogConfig)

    def start(self):
        self.updateDB()
        
        while True:
            data, client_address = self.socketUDP.recvfrom(4096)
            
            msg  = query(0,"", 0,0,0,0,"","", [],[],[])
            flag = True
            try:
                msg.decode(data)
            except: 
                response = query(0,"",3,0,0,0,"","",[],[],[])
                flag = False
                with self.lockUDP:
                    self.socketUDP.sendto(response.encode(),client_address)
                self.Logger.write(log(time.time(),"RP", f'{client_address[0]}', f'{response}'))
            

            if flag:
                if  msg.messageID in self.request_pool and msg.flags == "A+R" or msg.flags == "A" or msg.flags=="":
                    self.Logger.write(log(time.time(),"RR", "localhost", f'{msg}'))
                    self.rwlock.writerLock()
                    self.response_pool[msg.messageID] = msg
                    self.rwlock.writerUnlock()

                    self.event.set()

                elif msg.messageID not in self.request_pool:
                    self.Logger.write(log(time.time(),"QR", "localhost", f'{msg}'))
                    self.rwlock.writerLock()
                    self.request_pool.append(msg.messageID)
                    self.rwlock.writerUnlock()

                    t = threading.Thread(target=self.process_request, args=(msg, client_address))
                    t.start()

            elif msg.responseCode == 3 :
                response = query(0,"",3,0,0,0,"","",[],[],[])
                with self.lockUDP:
                    self.socketUDP.sendto(response.encode(),client_address)
                self.Logger.write(log(time.time(),"RP", f'{client_address[0]}', f'{response}'))

            else: 
                response = query(0,"",3,0,0,0,"","",[],[],[])
                with self.lockUDP:
                    self.socketUDP.sendto(response.encode(),client_address)
                self.Logger.write(log(time.time(),"RP", f'{client_address[0]}', f'{response}'))

        
    def process_request(self,request:query,client_address):
            
        if request.flags == "Q":
            response = self.iterative_process(request)   

        elif request.flags == "Q+R" :
            response = self.recursive_process(request)

        self.Logger.write(log(time.time(),"RP", f"{client_address[0]}", f'{response}'))
        with self.lockUDP:
            self.socketUDP.sendto(response.encode(),client_address)
        
        self.request_pool.remove(request.messageID)

        return


    def iterative_process(self,request : query): 
        
        response = self.getResponse(request)

        return response

    def recursive_process(self,request : query): 

        domains= request.qi_name.split(".")
        if(len(domains) in (3,2)) :
            dominio = domains[0] + "." 
        else: 
            return  query(request.messageID,"", 2,0,0,0, request.qi_name, request.qi_typeValue , [],[],[])


        r_request  = query(request.messageID,"Q", 0,0,0,0, request.qi_name, request.qi_typeValue, [],[],[])
        
        ip = ('10.0.13.10', 10000)
        #st_ip = (socket.gethostbyname(socket.gethostname()), 10000)
        
        if (self.config.serverType in ("ST1","ST2") and len(domains)==3):
            r_request.qi_typeValue = "NS" 

        with self.lockUDPsend :
            self.socketUDP.sendto(r_request.encode(), ip)
        self.Logger.write(log(time.time(),"QE", f'{ip}', f'{r_request}'))

        while(True):
            self.event.wait()
            if(self.in_responsepoll(request.messageID)) : 
                self.event.clear()
                break
        
        self.rwlock.writerLock()
        response = self.response_pool.pop(request.messageID)
        self.rwlock.writerUnlock()

        if(request.qi_name == self.config.dominio) :
            response.flags = "A"

        return response    



    ######################### AUXILIARES ###############################

    def getResponse(self, request : query):
        
        response = query(request.messageID,"",0,0,0,0,request.qi_name,request.qi_typeValue,[],[],[])
        
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


    def in_responsepoll(self, msgID):
        self.rwlock.readLock()
        cond = msgID in self.response_pool.keys() 
        self.rwlock.readUnlock()
        return cond

 ############# TRANSFERENCIA DE ZONA #################

    def updateDB(self):
        dbu_request = query(random.randint(0,65535),"Q",0,0,0,0,"DBU","DB")

        with self.lockUDP:
            #self.socketUDP.sendto(dbu_request.encode(), (self.config.sp[0][0], 9000)) 
            self.socketUDP.sendto(dbu_request.encode(), (self.ipHost, 9000)) 
        
        time.sleep(0.1)
        filepath = self.updateDBprotocol()
        
        if not filepath:
            self.Logger.write(log(time.time(),"EZ", "localhost", f'Failed TZ'))
            return    

        self.server_cache.readBD(filepath, origin.SP)
        os.remove(filepath)

        

    def updateDBprotocol(self):
        self.lockTCP.acquire()
        self.Logger.write(log(time.time(),"ZT",f'{self.config.sp[0][0]}','Start TZ'))
        portTCP = 9999 #porta TCP do servidor

        socketTCP = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
        #socketTCP.connect((self.config.sp[0][0],portTCP))
        socketTCP.connect((socket.gethostname(),portTCP))

        #envia o dominio
        msg = "dominio: " + self.config.dominio
        
        self.lockTCP.acquire()
        socketTCP.send(msg.encode('utf-8'))
        self.lockTCP.release()

        #recebe o "entries : nEntradas"
        self.lockTCP.acquire()
        msg_r1 = socketTCP.recv(BUFFERSIZE)
        self.lockTCP.release()

        msg_r1 = msg_r1.decode("utf-8")

        if msg_r1!= "TZ DENIED":
            noEntries = msg_r1.split(' ')[1]
        else: 
            print("TZ Failed")
            return 

        #envia o "ok : nEntradas"
        msg = "ok: "+ str(noEntries)
        self.lockTCP.acquire()
        socketTCP.send(msg.encode('utf-8'))
        self.lockTCP.release()

        name = self.config.dominio.split('.')[1]
        #core #filepath = "../temp/"+ name + ".db"
        filepath = "./temp/"+ name + ".db"
        file = open(filepath, 'w')
        
        while True:
            self.lockTCP.acquire()
            msg_rec = socketTCP.recv(BUFFERSIZE)
            self.lockTCP.release()

            msg_rec = msg_rec.decode("utf-8")
            
            file.write(msg_rec)
            if msg_rec.endswith('\r'): 
                break

        file.close()
        socketTCP.close()
        self.lockTCP.release()

        self.Logger.write(log(time.time(),"ZT",f'{self.config.sp[0][0]}','END TZ with success'))
        
        return filepath

 