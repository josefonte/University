import csv
from itertools import groupby
from operator import itemgetter
import re
import os
import re

def create_csv(program_name, data):
    # Replace spaces and special characters with underscores
    program_name = re.sub(r'[^\w]', '_', program_name)
    
    filename = f"java/{program_name}.csv"
    with open(filename, 'w', newline='') as csvfile:
        writer = csv.writer(csvfile)
        writer.writerow(['Language', 'Program', 'PowerLimit', 'Package', 'Core', 'GPU', 'DRAM', 'Time', 'Temperature', 'Memory'])
        writer.writerows(data)

def main():
    # Create the folder if it doesn't exist
    os.makedirs("java", exist_ok=True)
    
    with open('measurements_java.csv', 'r') as csvfile:
        reader = csv.reader(csvfile)
        next(reader)  # Skip header
        sorted_data = sorted(reader, key=itemgetter(1))  # Sort by program name
        for program_name, program_data in groupby(sorted_data, key=itemgetter(1)):
            create_csv(program_name, program_data)

if __name__ == "__main__":
    main()
