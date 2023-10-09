CC = gcc

all: server client 
server: sdstored
client: sdstore

sdstored: src/sdstored.c
	@ $(CC)  src/sdstored.c src/aux.c -o sdstored

sdstore: src/sdstore.c 
	@ $(CC) src/sdstore.c src/aux.c -o sdstore

clean:
	@ rm sdstore sdstored   
	
#obj/* tmp/* bin/{sdstore,sdstored}