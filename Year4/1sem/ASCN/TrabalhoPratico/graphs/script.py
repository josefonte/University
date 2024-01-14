import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

# Load the CSV file
file_path = ['1-10.csv','4-100.csv','4-1000.csv']
for csv in file_path:
    df = pd.read_csv(csv)
    plt.figure(figsize=(9, 6))
    
    # Plot 1: Average Response Time per Page
    plt.subplot(1, 3, 1)
    avg_response_time = df.groupby('label')['elapsed'].mean().sort_values()
    avg_response_time.plot(kind='bar', color='skyblue')
    plt.title('Average Response Time per Page')
    plt.ylabel('Average Response Time (ms)')
    
    # Plot 2: Latency Distribution
    plt.subplot(1, 3, 2)
    latency_distribution = df['Latency']
    sns.histplot(latency_distribution, kde=True, color='purple')
    plt.title('Latency Distribution')
    plt.xlabel('Latency (ms)')
    plt.ylabel('Frequency')
    
    # Plot 3: Response Time vs Group Threads
    plt.subplot(1, 3, 3)
    response_time_threads = df[['elapsed', 'grpThreads']]
    sns.scatterplot(x='grpThreads', y='elapsed', data=response_time_threads, color='orange')
    plt.title('Response Time vs Group Threads')
    plt.xlabel('Number of Group Threads')
    plt.ylabel('Response Time (ms)')
    
    plt.tight_layout()
    plt.show()