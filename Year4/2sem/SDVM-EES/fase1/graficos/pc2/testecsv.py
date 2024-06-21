import csv
from itertools import groupby
from operator import itemgetter
import re

def create_csv(program_name, power_limit, data):
    # Replace spaces and special characters with underscores
    program_name = re.sub(r'[^\w]', '_', program_name)
    power_limit = re.sub(r'[^\w]', '_', str(power_limit))
    
    filename = f"{program_name}_PowerLimit_{power_limit}.csv"
    with open(filename, 'w', newline='') as csvfile:
        writer = csv.writer(csvfile)
        writer.writerow(['Language', 'Program', 'PowerLimit', 'Package', 'Core', 'GPU', 'DRAM', 'Time', 'Temperature', 'Memory'])
        writer.writerows(data)

def main():
    with open('csvLucena.csv', 'r') as csvfile:
        reader = csv.reader(csvfile)
        next(reader)  # Skip header
        sorted_data = sorted(reader, key=itemgetter(1, 2))  # Sort by program name and power limit
        for (program_name, power_limit), program_data in groupby(sorted_data, key=itemgetter(1, 2)):
            create_csv(program_name, power_limit, program_data)

if __name__ == "__main__":
    main()