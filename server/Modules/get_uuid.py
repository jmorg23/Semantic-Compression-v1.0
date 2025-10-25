import uuid
import os

def get_uuid():
    uuid = str(uuid.uuid4())

    os.makedirs(f"server/users/{uuid}/conversations/", exist_ok=True)

    return uuid