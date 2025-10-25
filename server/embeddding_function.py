import requests

def embed(text):
    return requests.post("http://100.124.29.36:8000/embed", json={"text":text}).json()['vector']