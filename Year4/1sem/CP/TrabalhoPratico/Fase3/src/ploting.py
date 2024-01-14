import matplotlib.pyplot as plt
import numpy as np

# Data
sizes = [2500, 5000, 10000, 20000]
num_threads = [8, 16, 32, 256, 512, 1024]

config_1_times = [6.959, 6.281, 6.091, 8.059, 9.120, 12.313]
config_2_times = [11.949, 9.689, 12.9, 10.089, 14.080, 19.376]
config_3_times = [33.239, 24.993, 22.1, 22.023, 23.013, 22.839]
config_4_times = []

# Set up positions for bars on X axis
bar_width = 0.2
bar_positions = np.arange(len(sizes))

# Plotting the bar chart
plt.figure(figsize=(12, 8))

for i, config_times in enumerate([config_1_times, config_2_times, config_3_times, config_4_times]):
    plt.bar(bar_positions + i*bar_width, config_times, width=bar_width, label=f'Threads: {num_threads[i]}')

plt.title('Execution Time for Different Thread Configurations')
plt.xlabel('Number of Particles (N)')
plt.ylabel('Execution Time (s)')
plt.xticks(bar_positions + 1.5*bar_width, sizes)

# Set legend font size
plt.legend(title='Number of Threads per Block', fontsize='xx-large')

plt.grid(True)
plt.show()