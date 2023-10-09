from datetime import datetime
import socket, threading
from rwlock import ReadWriteLock
#{etiqueta temporal} {tipo de entrada} {endereço IP[:porta]} {dados da entrada} 

class log():

    def __init__(self,timestamp, typeEntry="", IPport="", dados=""):
        self.timestamp = timestamp
        self.typeEntry = typeEntry
        self.IPport = IPport
        self.dados = dados

    def __str__(self):
        time = datetime.fromtimestamp(self.timestamp).strftime("%d:%m:%Y.%H:%M:%S:%f")[:-3]
        return f'{time} {self.typeEntry} {self.IPport} {self.dados}'

    def format(self):
        time = datetime.fromtimestamp(self.timestamp).strftime("%d:%m:%Y.%H:%M:%S:%f")
        return f'{time} {self.typeEntry} {self.IPport} {self.dados}\n'


class LogFile():

    def __init__(self, log_file, allLog_file, debug):
        self.LG = open(log_file, "a")
        self.allLG = open(allLog_file, "a")
        self.debug = debug
        self.lock = threading.RLock()


    def write(self, log : log):
        self.lock.acquire()

        self.LG.write(log.format())
        if(self.debug) : print(log.format())       
         
        self.lock.release()
    
    def writeInALL(self, log : log):
        self.lock.acquire()

        self.allLG.write(log.format())
        if(self.debug) : print(log)       
         
        self.lock.release()


#perceber para que serve o ficheiro all log