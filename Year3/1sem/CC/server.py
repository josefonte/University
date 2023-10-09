import sys
from sr import serverSR
from sp import serverSP
from ss import serverSS
from config import config
from logs import log

#python3 server.py ./config_files/SR.conf 7000 0 10.0.12.10 DEBUG
#python3 server.py ./config_files/SS1-bra.conf 8000 0 DEBUG
#python3 server.py ./config_files/SP-bra.conf 9000 0 DEBUG


class InitServer():

    def __init__(self, config_filepath, port_str, timeout, ipHost,debug):
        if debug == "DEBUG" : debug_mode = True
        self.port = int(port_str)
        self.ipHost = ipHost
        self.timeout = int(timeout)

        server_config = config.parseConfig(config_filepath)
        
        if server_config.serverType == "SR": #SR
            return serverSR(server_config,self.port,self.timeout,self.ipHost,True).start() 
        elif server_config.serverType in ("SS1","SS2"): #SS
            return serverSS(server_config,self.port,self.timeout,self.ipHost,True).start()         
        elif server_config.serverType == "SP" :#SP
            return serverSP(server_config,self.port,self.timeout,self.ipHost,True).start() 
        elif server_config.serverType in ("ST1", "ST2"):#ST
            return serverSP(server_config,self.port,self.timeout,self.ipHost,True).start() 

def main():

    if len(sys.argv)<5 or len(sys.argv)>6: 
        print('Wrong number of arguments')
        return 0

    InitServer(sys.argv[1], sys.argv[2],sys.argv[3], sys.argv[4], sys.argv[5])


if __name__ == "__main__":
    main()