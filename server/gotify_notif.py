import requests

gotify_url = "http://100.124.29.36:8888/message"
gotify_token = "AOVOkVSMVK5Yyc6" 

def send_message(title: str, message: str, priority: int = 5):
    """
    Sends a message to the Gotify server.
    
    :param title: The title of the message.
    :param message: The content of the message.
    :param priority: The priority of the message (0, 5, or 10).
    """
    payload = {
        "title": title,
        "message": message,
        "priority": priority
    }
    
    response = requests.post(
        gotify_url,
        headers={"X-Gotify-Key": gotify_token},
        json=payload
    )
    
    return response

if __name__ == "__main__":
    response = send_message("Test Title", "This is a test message.")
    print(f"Response status code: {response.status_code}")