import socket
import sys
import random
from query import query
import logs
import time

BUFFERSIZE = 2048

class clientInput():
    
    def __init__(self, **kwargs):
        allowed_keys = set(['IPadress','qi_name','qi_typeValue', 'flag_R'])
        self.__dict__.update((key, False) for key in allowed_keys)
        self.__dict__.update((key, value) for key, value in kwargs.items() if key in allowed_keys)

    def __str__(self) :
        return f'IP[:endereço] = {self.IPadress},QUERY-INFO.NAME = {self.qi_name},QUERY-INFO.TYPE = {self.qi_typeValue},FLAG_R = {self.flag_R}'

    def parseClientInput(argv):
        if len(argv)==4:
            return clientInput(IPadress=argv[1],qi_name=argv[2], qi_typeValue=argv[3])
        elif len(argv)==5 and argv[4]=='R':
            return clientInput (IPadress=argv[1],qi_name=argv[2], qi_typeValue=argv[3],flag_R=True)
        


def main():
    if len(sys.argv)<4 or len(sys.argv)>5: 
        print('Wrong number of arguments')
        return 0
    
    #create_query
    inputMsg = clientInput.parseClientInput(sys.argv)

    if inputMsg.flag_R : 
        flags="Q+R"
    else :
        flags= "Q"
    
    dnsquery = query(random.randint(0,65535), flags, 0,0,0,0,inputMsg.qi_name,inputMsg.qi_typeValue)

    #send_query 

    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

    # SR - Porta: 7000
    # SS - Porta: 8000
    # SP - Porta: 9000


    s.sendto(dnsquery.encode(), (inputMsg.IPadress, 7000))
    
    #receive response
    server_response , addressServer = s.recvfrom(BUFFERSIZE)
    #decode personalizado
    response = query()
    response.decode(server_response)
    
    print(response)

    s.close()

if __name__ == "__main__":
    main()



