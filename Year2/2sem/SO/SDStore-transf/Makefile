CC = gcc
CFLAGS = -Wall -g

all: nop gcompress gdecompress bcompress bdecompress encrypt decrypt

nop: nop.o
	@ $(CC) $(CFLAGS)  nop.o  -o ../bin/nop
nop.o: nop.c

gcompress: gcompress.o
	@ $(CC) $(CFLAGS)  gcompress.o  -o ../bin/gcompress
gcompress.o: gcompress.c

gdecompress: gdecompress.o
	@ $(CC) $(CFLAGS)  gdecompress.o  -o ../bin/gdecompress
gdecompress.o: gdecompress.c

bcompress: bcompress.o
	@ $(CC) $(CFLAGS)  bcompress.o  -o ../bin/bcompress
bcompress.o: bcompress.c

bdecompress: bdecompress.o
	@ $(CC) $(CFLAGS)  bdecompress.o  -o ../bin/bdecompress
bdecompress.o: bdecompress.c

encrypt: encrypt.o
	@ $(CC) $(CFLAGS)  encrypt.o  -o ../bin/encrypt
encrypt.o: encrypt.c

decrypt: decrypt.o
	@ $(CC) $(CFLAGS)  decrypt.o  -o ../bin/decrypt
decrypt.o: decrypt.c


clean:
	rm -f *compress *decompress encrypt decrypt nop *.o
	rm ../bin/*
