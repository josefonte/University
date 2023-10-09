import socket, sys, time, re
from os.path import exists
from query import query
from logs import log
from config import config
from cache import cache, origin
from logs import log, LogFile
from rwlock import ReadWriteLock
import threading

#PORTA de receção UDP do SP = 9000
BUFFERSIZE = 2048

class serverSP():

    def __init__(self,server_config : config, port=9000, timeout=0, ipHost="", debug = True):
        LogInit = log(time.time(),"ST", "127.0.0.1", f'{port} {timeout} Debug={debug}')
        self.config = server_config
        LogConfig = log(time.time(),"EV", "localhost", f'conf-file-read')
        self.ipHost = ipHost
        self.port = port
        self.timeout = timeout
        self.server_cache = cache(20)
        self.Logger = LogFile(self.config.lg, self.config.all_lg, debug)
        self.socketUDP = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.lockUDPsend = threading.RLock() 
        self.lockTCP = threading.Lock()

        self.socketUDP.bind((self.ipHost, self.port))

        self.request_pool = list()
        self.response_pool = dict() 
        self.rwlock = ReadWriteLock()
        self.event = threading.Event()

        self.Logger.write(LogInit)
        self.Logger.write(LogConfig)

    def start(self):

        self.server_cache.readBD(self.config.db,origin.FILE)
        
        self.Logger.write(log(time.time(),"EV", 'localhost', 'database-read'))
            
        while True:
            data, client_address = self.socketUDP.recvfrom(4096)
            
            msg  = query(0,"", 0,0,0,0,"","", [],[],[])
            flag = True
            try:
                msg.decode(data)
            except: 
                response = query(0,"",3,0,0,0,"","",[],[],[])
                flag = False
                with self.lockUDPsend:
                    self.socketUDP.sendto(response.encode(),client_address)

            if flag:
                if  msg.messageID in self.request_pool and msg.flags == "A+R" or msg.flags == "A" or msg.flags=="":
                    self.Logger.write(log(time.time(),"RR", f"{client_address[0]}", f'{msg}'))
                    self.rwlock.writerLock()
                    self.response_pool[msg.messageID] = msg
                    self.rwlock.writerUnlock()

                    self.event.set()

                elif msg.messageID not in self.request_pool:
                    self.Logger.write(log(time.time(),"QR", f"{client_address[0]}", f'{msg}'))
                    self.rwlock.writerLock()
                    self.request_pool.append(msg.messageID)
                    self.rwlock.writerUnlock()

                    t = threading.Thread(target=self.process_request, args=(msg, client_address))
                    t.start()

            elif msg.responseCode == 3 :
                response = query(0,"",3,0,0,0,"","",[],[],[])
                with self.lockUDPsend:
                    self.socketUDP.sendto(response.encode(),client_address)
                self.Logger.write(log(time.time(),"RP", f'{client_address[0]}', f'{response}'))


            else: 
                response = query(0,"",3,0,0,0,"","",[],[],[])
                with self.lockUDPsend:
                    self.socketUDP.sendto(response.encode(),client_address)
                self.Logger.write(log(time.time(),"RP", f'{client_address[0]}', f'{response}'))



    def process_request(self,request:query,client_address):

        if request.flags == "Q":
            response = self.iterative_process(request)
            if response == "DBU":
                self.request_pool.remove(request.messageID)
                return

        if request.flags == "Q+R" :
            response = self.recursive_process(request)
            
        if self.config.serverType in ("ST1", "ST2"):
            response.flags = ""

        with self.lockUDPsend:
            self.socketUDP.sendto(response.encode(), client_address)

        self.Logger.write(log(time.time(),"RP", f'{client_address[0]}', f'{response}'))

        self.request_pool.remove(request.messageID)


        return


    def iterative_process(self,request : query):

        domains= request.qi_name.split(".")
        if(len(domains) in (3,2)) :
            dominio = domains[0] + "." 
        elif not (request.qi_typeValue == "DB"): 
            return  query(request.messageID,"", 2,0,0,0, request.qi_name, request.qi_typeValue , [],[],[])


        if request.qi_typeValue == "DB" :
            if request.qi_name == "DBU": #DB update - transferencia de zona
                self.sendDBprotocol() #implementar o timeout na TZ
                return "DBU"

            elif request.qi_name == "DBV": # DB version
                dbv_list = self.server_cache.search((self.config.dominio, "SOASERIAL"))
                dbv = dbv_list[0].format()
                lista  = list().append(dbv)
                response = query(request.messageID,"A", 0,0,0,0,request.qi_name,request.qi_typeValue, lista,[],[])
                return response

        response = self.getResponse(request)
        
        if(request.qi_name == self.config.dominio) :
            response.flags = "A"

        return response   
        
    def recursive_process(self,request : query):
        domains= request.qi_name.split(".")
        if(len(domains) in (3,2)) :
            dominio = domains[0] + "." 
        else: 
            return  query(request.messageID,"", 2,0,0,0, request.qi_name, request.qi_typeValue , [],[],[])

        if (dominio == self.config.dominio):
             response = self.getResponse(request)
             return response
	
        ip_aux = self.getIpReq(request)
        #core #ip = ('10.0.13.10', 10000)

        r_request  = query(request.messageID,"Q+R", 0,0,0,0, request.qi_name, request.qi_typeValue, [],[],[])
        
        ip = (ip_aux, 9000)
        

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

    ################################### FUNÇÕES AUXILIARES ###################################     

    def getResponse(self, request : query):
        
        if (self.config.serverType in  ["ST1", "ST2"] and len(request.qi_name.split('.'))==3):
                response = query(request.messageID,"",0,0,0,0,request.qi_name.split('.')[1]+".",request.qi_typeValue,[],[],[])
        else :
            response = query(request.messageID,"",0,0,0,0,request.qi_name,request.qi_typeValue,[],[],[])

        responseV = []
        authV = []
        extraV = []
        extraValues = set()
        
        responseList = self.server_cache.search((response.qi_name,request.qi_typeValue),0) 
        
        for respElem in responseList:
            extraValues.add(respElem.value.replace(response.qi_name, ""))
            responseV.append(respElem.format())

        response.noValues = len(responseV)
        if len(responseV) == 0 : 
            response.responseCode = 1
        response.responseValues = responseV
        
        authList = self.server_cache.search((response.qi_name,"NS"),0)
       
        for authElem in authList:
            extraValues.add(authElem.value.replace(response.qi_name, ""))
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

    ################################### TRANSFERENCIA DE ZONA ###################################

    def sendDBprotocol(self):
        self.lockTCP.acquire()
        portTCP = 9999
        socketTCP = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        #core #socketTCP.bind((self.ipHost, portTCP))
        socketTCP.bind((socket.gethostname(), portTCP))
        socketTCP.listen()

        ss_socket, SSadress = socketTCP.accept()
        self.Logger.write(log(time.time(),"ZT",f'{SSadress[0]}','Start TZ'))

        #recebe domain
        msg_r1 = ss_socket.recv(BUFFERSIZE)
        msg_r1 = msg_r1.decode("utf-8")     
        domain = msg_r1.split(' ')[1]
        

        if self.config.dominio == domain :#core #and SSadress[0] in (self.config.ss[0][0], self.config.ss[1][0]): 
            #envia entries: nº de entradas
            bdfile = open(self.config.db, 'r')
            msg_entries = "entries: "+ str(len(bdfile.readlines()))
            ss_socket.send(msg_entries.encode('utf-8'))
            bdfile.close()
        else : 
            msg_entries = "TZ DENIED"
            ss_socket.send(msg_entries.encode('utf-8'))
            ss_socket.close()
            socketTCP.close()
            return False

        #recebe ok: nentradas
        msg_r2 = ss_socket.recv(BUFFERSIZE)
        msg_r2 = msg_r2.decode("utf-8")
        ok_entries = int(msg_r2.split(' ')[1])

        #envia nentradas
        bdfile = open(self.config.db, 'r')

        i = 0
        while i<ok_entries:
            i+=1
            line = bdfile.readline()
            msg = str(i) + " : " + line
            if i == ok_entries:
                line += "\r"
            ss_socket.send(line.encode('utf-8')) 


        bdfile.close()
        ss_socket.close()
        socketTCP.close()
        self.lockTCP.release()

        self.Logger.write(log(time.time(),"ZT",f'{SSadress[0]}','END TZ with success'))
        


    def in_responsepoll(self, msgID):
        self.rwlock.readLock()
        cond = msgID in self.response_pool.keys() 
        self.rwlock.readUnlock()

        return cond


    def getIpReq(self, request : query):
        ip = ""
        if (self.config.serverType in  ["ST1", "ST2"] and len(request.qi_name.split('.'))==3):
            response = query(request.messageID,"",0,0,0,0,request.qi_name.split('.')[1]+".",request.qi_typeValue,[],[],[])
        else :
            response = query(request.messageID,"",0,0,0,0,request.qi_name,request.qi_typeValue,[],[],[])
        
        
        extraValues = set()
        
        responseList = self.server_cache.search((response.qi_name,request.qi_typeValue),0)
        
        for respElem in responseList:
            extraValues.add(respElem.value.replace(response.qi_name, ""))
        #------------------------------------------
        authList = self.server_cache.search((response.qi_name,"NS"),0)
        for authElem in authList:
            extraValues.add(authElem.value.replace(response.qi_name, ""))
        #------------------------------------------
        for a in extraValues:
            a= a[:-1]
            if a in ["ns1","ns4","sp"]:
                newExtraV = self.server_cache.search((a,"A"),0)
                ip = newExtraV[0].format().split(" ")[2]
        
        return ip
