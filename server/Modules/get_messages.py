import os
import json

def get_messages(user_uuid:str, conversation_uuid:str):
    users = os.listdir("server/users/")
    if user_uuid not in users:
        return None
    else:
        conversations = os.listdir(f"server/users/{user_uuid}/conversations/")
        if conversation_uuid not in conversations:
            return None
        else:
            os.listdir(f"server/users/{user_uuid}/conversations/{conversation_uuid}/")
            if "conversation.json" not in os.listdir(f"server/users/{user_uuid}/conversations/{conversation_uuid}/"):
                return None
            else:
                with open(os.path.join("server", "users", user_uuid, "conversations", conversation_uuid, "conversation.json"), "r", encoding="utf-8") as f:
                    data = json.load(f)
                    return data.get("messages", [])