#!/bin/bash
#SBATCH --partition=cpar
#SBATCH --cpus-per-task=40
#SBATCH --time=10:00
THREADS="2 4 8"

for thread in $THREADS; do
    export OMP_NUM_THREADS=$thread
    perf stat -r 1 -e instructions,cycles,L1-dcache-load-misses ./MDpar.exe < inputdata.txt
done