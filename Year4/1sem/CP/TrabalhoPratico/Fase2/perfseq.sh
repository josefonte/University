#!/bin/bash
# Path: ProjetoCP/Fase2/perf.sh

perf stat -r 1 -e instructions,cycles,L1-dcache-load-misses ./MDseq.exe < inputdata.txt

