CC = gcc
SRC = src/
EXAMPLE_FILES = example_files/
CFLAGS = -pg
OPT_FILES = opt_files/
CFLAGS_OPT = -pg -O3  -msse4 -mavx -mfpmath=sse -march=x86-64

.DEFAULT_GOAL = MD.exe

test0: 
	$(CC) $(CFLAGS) $(SRC)MD.cpp -lm -o $(EXAMPLE_FILES)MD.exe
#	$(EXAMPLE_FILES)MD.exe < inputdata.txt
#	gprof $(EXAMPLE_FILES)MD.exe gmon.out > $(EXAMPLE_FILES)gprofOUT.txt
	srun --partition=cpar perf stat -r 1 -e instructions,cycles $(EXAMPLE_FILES)MD.exe < inputdata.txt
	
test1:
	$(CC) $(CFLAGS_OPT) $(SRC)MD_opt.cpp -lm -o $(OPT_FILES)MD_opt.exe
#	$(OPT_FILES)MD_opt.exe < inputdata.txt
#	gprof $(OPT_FILES)MD_opt.exe gmon.out > $(OPT_FILES)gprof_opt_OUT.txt
	srun --partition=cpar perf stat -r 1 -e instructions,cycles,L1-dcache-load-misses  $(OPT_FILES)MD_opt.exe < inputdata.txt
#	srun --partition=cpar perf stat -r 1 -e L1-dcache-load-misses -M cpi  $(OPT_FILES)MD_opt.exe < inputdata.txt

MD.exe: $(SRC)/MD.cpp
	$(CC) $(CFLAGS) $(SRC)MD_opt.cpp -lm -o MD.exe

clean:
	rm ./MD.exe
	rm $(OPT_FILES)*

run:
	./MD.exe < inputdata.txt
