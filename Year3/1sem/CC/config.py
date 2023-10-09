import re

class config():

    def __init__(self, serverType = "",dominio="", db="", dd="", lg="",all_lg="", root_st="", sp=[], ss=[]):
        self.serverType =  serverType
        self.dominio = dominio
        self.db = db
        self.sp = sp
        self.ss = ss
        self.dd = dd
        self.lg = lg
        self.all_lg = all_lg
        self.root_st = root_st
    
    def __str__(self):
        return f"ServerType: {self.serverType};\nDominio: {self.dominio};\nDB: {self.db};\nSP: {self.sp};\nSS:{self.ss};\nDD:{self.dd};\nLG:{self.lg};\nALL_LG:{self.all_lg};\nroot:{self.root_st};"

    def parseConfig(filepath):
        config_server = config()

        path = filepath.split('/') 
        filename = path[-1] 

        if not filename in  ["ST1.conf", "ST2.conf"]  :
            file_args = filename.split('-')
            config_server.serverType = file_args[0]
            file_args[1] = file_args[1].split('.')
            config_server.dominio = f'{file_args[1][0]}.' 
        else : 
            file_args = filename.split('.')
            config_server.serverType = file_args[0]
            config_server.dominio = "." 


        with open(filepath, 'r') as file:
            lines = file.readlines()

        for line in lines:
            args = line.replace('\n', ' ').split(' ')
            
            if args[0]!='#':
                if args[0]=='all' and args[1]=='LG':
                    config_server.all_lg = args[2]
                
                elif args[0]=='root' and args[1]=='ST':
                    config_server.root_st = args[2]

                elif args[0]==config_server.dominio:
                    if args[1]=='DB':
                        config_server.db = args[2]

                    elif args[1]=='SP':
                        addSP = (args[2],9000)
                        config_server.sp.append(addSP)
                    
                    elif args[1]=='SS':
                        addSS = (args[2],8000)
                        config_server.ss.append(addSS)
                    
                    elif args[1]=='DD':
                        config_server.dd = args[2]

                    elif args[1]=='LG':
                        config_server.lg = args[2]

                else : 
                    return "Failed"                

        return config_server

