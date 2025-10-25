import os
import json

def get_conversations(user_uuid: str):
    
    base_path = os.path.join("server", "users", user_uuid, "conversations")

    # Ensure user directory exists
    os.makedirs(base_path, exist_ok=True)

    # If no conversations yet, return empty list
    if not os.path.isdir(base_path):
        return []

    conversations = []

    for convo_uuid in os.listdir(base_path):
        convo_path = os.path.join(base_path, convo_uuid)
        if not os.path.isdir(convo_path):
            continue  # skip files, only process folders

        metadata_path = os.path.join(convo_path, "metadata.json")

        # Default name in case metadata is missing or invalid
        convo_name = "(untitled)"

        if os.path.exists(metadata_path):
            try:
                with open(metadata_path, "r", encoding="utf-8") as f:
                    metadata = json.load(f)
                    convo_name = metadata.get("conversation_name", convo_name)
            except (json.JSONDecodeError, OSError):
                pass  # ignore broken metadata files

        conversations.append({
            "uuid": convo_uuid,
            "name": convo_name
        })

    # Sort by last modified time (newest first)
    conversations.sort(
        key=lambda c: os.path.getmtime(os.path.join(base_path, c["uuid"])),
        reverse=True
    )

    return conversations
