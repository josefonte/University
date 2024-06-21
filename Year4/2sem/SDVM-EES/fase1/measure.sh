#!/bin/bash
NTIMES=10

export SEED="$seed"
export N="$n"

#Compile sensors wich will be used to calculate cool temperature
cd RAPL
gcc -shared -o sensors.so sensors.c
cd ..

#Update the temperature value
cd Utils/
python3 temperatureUpdate.py

#Update the number of times the program will run on each case TODO PRECISO ATUALIZAR ISTO PARA TODOS OS PROGRAMAS
for language in "../Languages"/*; do
    for program in "$language"/*; do
        if [ -d "$program" ]; then
            makefile_path="$program/Makefile"
            if [ -f "$makefile_path" ]; then
                python3 ntimesUpdate.py "$NTIMES" "$makefile_path"
            else
                echo "Makefile not found: $makefile_path"
            fi
        fi
    done
done
cd ..
      
echo "Language,Program,PowerLimit,Package,Core,GPU,DRAM,Time,Temperature,Memory" > measurements.csv

# verificar no site da intel o min e o max do processador
# Loop over power limit values
for limit in -1 12 15 25 47 55 

    do
    cd Utils/
    python3 raplCapUpdate.py $limit ../RAPL/main.c
    cd ..
    #Make RAPL lib
    cd RAPL/
    rm -f sensors.so
    make
    cd ..

    for language in "Languages"/*; do
    #for language in "Languages/Python";do
        for program in "$language"/*; do
            if [ -d "$program" ]; then
                makefile_path="$program/Makefile"
                if [ -f "$makefile_path" ]; then
                    cd $program
                    make compile
                    make measure 
                    # Specify the input file name
                    file="measurements.csv"
                    tail -n +2 "$file" >> ../../../measurements.csv;
                    make clean
                    cd ../../..
                else
                    echo "Makefile not found: $makefile_path"
                fi
            fi
        done
    done
done

cd RAPL/
make clean
cd ..

#sudo reboot