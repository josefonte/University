import json
import requests

def load_json_file(file_path):
    with open(file_path, 'r') as file:
        data = json.load(file)
    return data

def post_data(endpoint, data):
    base_url = 'http://localhost:7778/api/cc/'  # Update with the actual base URL of your app
    url = f'{base_url}{endpoint}'
    headers = {'Content-Type': 'application/json'}

    for item in data:
        print(item)
        response = requests.post(url, json=item, headers=headers)
        
        if response.status_code == 201:
            print(f'Successfully created {endpoint}: {response.json()}')
        else:
            print(f'Failed to create {endpoint}. Status code: {response.status_code}, Response: {response.text}')

if __name__ == "__main__":
    dataset_path = 'dataset.json'
    dataset = load_json_file(dataset_path)

    # Example: Posting data for 'prova'
    post_data('prova', dataset['prova'])

    # Example: Posting data for 'tipoQuestao'
    post_data('tipoquestao', dataset['tipoQuestao'])

    # Example: Posting data for 'questao'
    post_data('questao', dataset['questao'])
