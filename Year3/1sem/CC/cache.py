import time
from enum import Enum
from datetime import datetime
from rwlock import ReadWriteLock

class state(Enum):
    VALID = 0
    FREE = 1

class origin(Enum):
    FILE = 0
    SP = 1
    OTHERS = 2
    NONE = 3

class cacheEntry():
    def __init__(self, name = "" , typeValue= "", value = ""  , ttl = 0 , priority=0, origin=origin.NONE, timestamp=time.time(), index=0, state=state.FREE):
        self.name = name
        self.typeValue = typeValue
        self.value = value
        self.ttl = ttl
        self.priority = priority
        self.origin = origin #FILE, SP ou OTHER
        self.timestamp = timestamp
        self.index = index
        self.state = state

    def __str__(self):
        return f'{self.state} | Index = {self.index} | Timestamp = {str(datetime.fromtimestamp(self.timestamp))} | Name = {self.name} | TypeValue = {self.typeValue} | Value = {self.value}  | TTL = {self.ttl}  | Priority =  {self.priority} | Origin = {self.origin}'
        
    def __eq__(self, entry): 
        if not isinstance(entry, cacheEntry):
            return NotImplemented
        return self.name == entry.name and self.typeValue == entry.typeValue and self.value == entry.value and self.ttl == entry.ttl and self.origin==entry.origin
    
    def format(self):
        return f'{self.name} {self.typeValue} {self.value} {self.ttl} {self.priority}'


class cache():

    #função que cria cache de X size com um timestamp de quando foi iniciada
    def __init__(self,size):
        self.entries = [cacheEntry() for i in range(size)]
        self.timestamp = time.time()
        self.lock = ReadWriteLock()

    def __str__(self):
        string=""
        string += str(datetime.fromtimestamp(self.timestamp)) + '\n'
        for entry in self.entries:
            string += "----------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n"
            string += str(entry) + '\n'

        return string

    #procura entrada valid apartir de 3 valores - index, name, type
    #recebe tuplo[name,type] e o index
    def search(self,pair,index) :
        self.lock.readLock()
        
        name,typeValue = pair
        listVALIDS = []

        for entry in self.entries:
            if entry.name == name and entry.typeValue == typeValue and entry.index>=index:
                listVALIDS.append(entry)
            if entry.origin == origin.OTHERS:
                current_timestamp = time.time()
                if (current_timestamp - entry.timestamp)>=float(entry.ttl) : entry.state = state.FREE

        self.lock.readUnlock()

        return listVALIDS
    
    #registar or atualizar uma entry
    # se origin for FILE ou SP. encontrar a primeira entrada livre- e regista aí
    #se origin for OTHERs.
    def register(self, new_entry):
        self.lock.writerLock()
        i = 0
        if new_entry.origin == origin.FILE or new_entry.origin == origin.SP:
           
            for entry in self.entries:
                i += 1
                if entry.state == state.FREE :
                    entry.state = state.VALID
                    entry.name = new_entry.name
                    entry.typeValue = new_entry.typeValue
                    entry.value = new_entry.value
                    entry.ttl = new_entry.ttl
                    entry.priority = new_entry.priority
                    entry.origin = new_entry.origin
                    entry.timestamp = time.time()
                    entry.index = i
                    self.lock.writerUnlock()
                    return entry.index
            
            
            #caso não haja entradas free entao adiciona nova entrada
            new_entry.timetamp = time.time()
            new_entry.state = state.VALID
            new_entry.index= i            
            self.entries.append(new_entry)
            self.lock.writerUnlock()
            return new_entry.index

        elif new_entry.origin == origin.OTHERS:
            #procurar se ja existe ou não entradas semelhantes
            #se sim, sp e file  na origin leva o registo a ser ignorado
            # other na origin, muda-se timestamp e muda-se state para valid
            for entry in self.entries :
                i+=1
                if entry == new_entry:
                    if entry.origin == origin.FILE or entry.origin == origin.SP:
                        self.lock.writerUnlock()
                        return 
                    if entry.origin== origin.OTHERS:
                        entry.state = state.VALID
                        entry.timestamp = time.time()
                        entry.index = i 
                        self.lock.writerUnlock()
                        return entry.index
            #se não existir igual então adiciona na ultima posição free
            flagFree=0
            for i in range(len(self.entries)):

                if self.entries[i].state == state.FREE: 
                    flagFree=1

                if i==len(self.entries)-1 and flagFree:
                    self.entries[i].state = state.VALID
                    self.entries[i].name = new_entry.name
                    self.entries[i].typeValue = new_entry.typeValue
                    self.entries[i].value = new_entry.value
                    self.entries[i].ttl = new_entry.ttl
                    self.entries[i].priority = new_entry.priority
                    self.entries[i].origin = new_entry.origin
                    self.entries[i].timestamp = time.time()
                    self.entries[i].index = i
            
            if not flagFree:

                new_entry.timetamp = time.time()
                new_entry.state = state.VALID
                new_entry.index = len(self.entries)
                self.entries.append(new_entry)
                self.lock.writerUnlock()
                return new_entry.index
            self.lock.writerUnlock()
            
        else : 
            self.lock.writerUnlock()
            return False


    def clean(self, name=""):
        self.lock.writerLock()
        for entry in self.entries:
            if entry.name == name:
                entry.state = state.FREE
        self.lock.writerUnlock()

    def readBD(self, dbpath, origin):
        
        dic = {}
        
        self.lock.readLock()
        with open(dbpath,'r') as file:
            lines = file.readlines()
        file.close()
        self.lock.readUnlock()
        

        for line in lines:
            entry = cacheEntry()
            args = line.replace('\n', ' ').split(' ')

            if args[0]!='#' and args[0]!='':
                if args[1]=='DEFAULT':
                    dic[args[0]]=args[2]
                    
                elif args[1] in ['SOASP','SOAADMIN','SOASERIAL','SOAREFRESH','SOARETRY','SOAEXPIRE','NS','A','CNAME','MX','P  TR']:
                    
                    if dic.get(args[0])!=None:
                        args[0]=dic[args[0]]
                    if dic.get(args[3])!=None:
                        args[3]=dic[args[3]]
                    
                    entry.name = args[0]
                    entry.typeValue = args[1]
                    entry.value = args[2]
                    if len(args)> 4:
                        
                        entry.ttl = args[3] 

                    
                    if len(args)>= 5:
                        if args[4]!='':
                            entry.priority=args[4]
                        
                    entry.origin = origin
                    entry.timestamp = time.time()
                    entry.index = 0
                    entry.state = state.VALID
                    
                    self.register(entry)
                
                else : 
                    return "Failed"
