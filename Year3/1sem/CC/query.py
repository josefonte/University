import  struct, bitstring
from bitarray import bitarray
from bitstring import BitArray

class query():

    def __init__(self, messageID = 0, flags="",responseCode=0,noValues=0,noAuthorities=0,noExtraValues=0,qi_name="",qi_typeValue="",responseValues=[],authValues=[],extraValues=[]):
        #Header Fields
        self.messageID = messageID
        self.flags = flags 
        self.responseCode = responseCode
        self.noValues = noValues
        self.noAuthorities = noAuthorities
        self.noExtraValues = noExtraValues
        #Query Info
        self.qi_name = qi_name
        self.qi_typeValue = qi_typeValue
        #Data Fields -> list(strings)
        self.responseValues = responseValues 
        self.authValues = authValues
        self.extraValues = extraValues

    def __str__(self):
        s0 = "\n#Header\n"
        s1 = f'MESSAGE-ID = {self.messageID}, '
        s2 = f'FLAGS = {self.flags}, '
        s3 = f'RESPONSE CODE = {self.responseCode},\n'
        s4 = f'N-VALUES = {self.noValues}, '
        s5 = f'N-AUTHORITIES = {self.noAuthorities}, '
        s6 = f'N-EXTRA-VALUES = {self.noExtraValues};\n'
        
        s7 = "# Data: Query Info\n"
        s8 = f'QUERY-INFO.NAME = {self.qi_name}, '
        s9 = f'QUERY-INFO.TYPE = {self.qi_typeValue};\n'
        
        s10 = "# Data: List Response, Authorities and Extra Values"
        
        string = s0+s1+s2+s3+s4+s5+s6+s7+s8+s9+s10

        if int(self.noValues)==0:
            if(self.flags in ("","A", "A+R")): 
                string += "\n# No direct response for this domain on this server"
            string += '\nRESPONSE-VALUES = (NULL)'
        else:    
            for index in range(len(self.responseValues)):
                line = self.responseValues[index]
                if index != (len(self.responseValues)-1) :
                    string += '\nRESPONSE-VALUES = ' + line + ','
                else:
                    string += '\nRESPONSE-VALUES = ' + line + ';'

        if int(self.noAuthorities)==0:
            string += '\nAUTHORITIES-VALUES = (NULL)'
        else:  
            for index in range(len(self.authValues)):
                line = self.authValues[index]
                if index != (len(self.authValues)-1) :
                    string += '\nAUTHORITIES-VALUES = ' +  line + ','
                else:
                    string += '\nAUTHORITIES-VALUES = ' + line + ';'
                        
        if int(self.noExtraValues)==0:
            string += '\nEXTRA-VALUES = (NULL)'
        else:  
            for index in range(len(self.extraValues)):
                line = self.extraValues[index]
                if index != (len(self.extraValues)-1) :
                    string += '\nEXTRA-VALUES = ' +  line + ','
                else:
                    string += '\nEXTRA-VALUES = ' + line + ';'


        return string

   

    def parseQuery(dnsString):
        
        args = dnsString.replace('\n', ' ').split(';')

        dnsQuery = query()

        header = args[0]
        args_header = header.split(',')
        dnsQuery.messageID =args_header[0]
        dnsQuery.flags = args_header[1]
        dnsQuery.responseCode = args_header[2]
        dnsQuery.noValues = args_header[3]
        dnsQuery.noAuthorities = args_header[4]
        dnsQuery.noExtraValues =args_header[5]
        
        query_info= args[1]
        args_qi = query_info.split(',')
        dnsQuery.qi_name = args_qi[0]
        dnsQuery.qi_typeValue = args_qi[1]

        if(int(dnsQuery.noValues) != 0):
            args_values = args[2].split(',')
            dnsQuery.responseValues.extend(args_values)

        if(int(dnsQuery.noAuthorities) != 0):
            args_authorities = args[3].split(',')
            dnsQuery.authValues.extend(args_authorities)
        
        if(int(dnsQuery.noExtraValues) != 0):
            args_extravalues = args[4].split(',')
            dnsQuery.extraValues.extend(args_extravalues)
         

        return dnsQuery

    def format(self):
        string = f'{self.messageID},{self.flags},{self.responseCode},{self.noValues},{self.noAuthorities},{self.noExtraValues};{self.qi_name},{self.qi_typeValue};'
            
        length = len(self.responseValues)
        for index in range(length):
            line = self.responseValues[index]
            if index != (length-1) :
                string += line + ','
            else:
                string += line + ';'
                    
        
        length= len(self.authValues)
        for index in range(length):
            line = self.authValues[index]
            if index != (length-1) :
                string += line + ','
            else:
                string += line + ';'
                    
        
        length= len(self.extraValues)
        for index in range(length):
            line = self.extraValues[index]
            if index != (length-1) :
                string += line + ','
            else:
                string += line + ';'


        return string
    
    def encode(self):
        msg = bitarray()
        msg += query.__int_to_bitarray(self.messageID,16)
        msg += query.__flag_to_bitarray(self.flags) 
        msg += query.__int_to_bitarray(self.responseCode,2)
        msg += query.__int_to_bitarray(self.noValues,8)
        msg += query.__int_to_bitarray(self.noAuthorities,8) 
        msg += query.__int_to_bitarray(self.noExtraValues,8) 
        #QUERY INFO
        msg += query.__qi_to_bitarray(self.qi_name)
        msg += query.__qi_to_bitarray(self.qi_typeValue) 
        
        if(self.noValues>0):
            msg += query.__utf8_to_bitarray(self.responseValues) 
        if(self.noAuthorities>0): 
            msg += query.__utf8_to_bitarray(self.authValues) 
        if(self.noExtraValues>0):
            msg += query.__utf8_to_bitarray(self.extraValues) 

        return msg.tobytes()

    def decode(self,msg):
        msg = BitArray(bytes=msg)
        mid = msg[:16]
        self.messageID = query.__bitarray_to_int(mid)
        f = msg[16:19]
        self.flags = query.__bitarray_to_flag(f)
        r = msg[19:21]
        self.responseCode = query.__bitarray_to_int(r)
        nv = msg[21:29]
        self.noValues = query.__bitarray_to_int(nv)
        na = msg[29:37]
        self.noAuthorities = query.__bitarray_to_int(na)
        ne = msg[37:45]
        self.noExtraValues = query.__bitarray_to_int(ne)

        #Query Info
        
        rest = query.__bitarray_to_utf8(msg[45:])

        args = rest.split(';')
        self.qi_name = args[0]
        self.qi_typeValue = args[1]
        #Data Fields 
        if(self.noValues>0):
            rv = args[2].split(',')
            self.responseValues.extend(rv) 
        if(self.noAuthorities>0): 
            av = args[3].split(',')
            self.authValues.extend(av) 
        if(self.noExtraValues>0):
            ev = args[4].split(',')
            self.extraValues.extend(ev)

    

    #FUNÇÕES AUXILIARES

    def __utf8_to_bitarray(n):
        string = ''
        length = len(n)
        for index in range(length):
            line = n[index]
            if index != (length-1) :
                string += line + ','
            else:
                string += line + ';'
        utf = string.encode('utf-8')
        ba = bitarray()
        ba.frombytes(utf)
        return ba

    def __qi_to_bitarray(n):
        n += ';'
        utf = n.encode('utf-8')
        ba = bitarray()
        ba.frombytes(utf)
        return ba
    

    def __bitarray_to_utf8(ba):
    
        b = ba.tobytes()
        return b.decode('utf-8')


    def __int_to_bitarray(num,n):
        s = bin(num)[2:]  
        s = s.zfill(n)
        r = bitarray(s)
        return r

    def __bitarray_to_int(ba):
        s = ''.join(['1' if b else '0' for b in ba])
        return int(s, 2)


    def __flag_to_bitarray(n):
        
        if   n == '':
            r = bitarray('000')
        elif n == 'Q+R' or n == 'R+Q':
            r = bitarray('110')    
        elif n == 'A':
            r = bitarray('001')    
        elif n == 'A+R' or n =='R+A':
            r = bitarray('011')
        elif n == 'Q':
            r = bitarray('100')
        return r

    def __bitarray_to_flag(n):
        if   n == bitarray('000'):
            r = ''
        elif n == bitarray('110'):    
            r = 'Q+R'
        elif n == bitarray('001'):    
            r = 'A'
        elif n == bitarray('011'):
            r = 'A+R'
        elif n == bitarray('100'):
            r = 'Q'
        return r