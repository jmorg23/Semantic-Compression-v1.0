import os
import json

from groq import Groq
# from groq_api_key import key
import uuid

model = "llama-3.3-70b-versatile"

client = Groq(
    api_key="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"
)

def start_conversation(user_uuid:str, starter_prompt:str):
    summary = client.chat.completions.create(
                messages=[{"role": "system", "content": "You are a helpful assistant."},
                          {"role": "user", "content": f"Make a short conversation title for the following starter prompt: {starter_prompt}"}],
                model=model,
                max_tokens=2048,
                stream=False,
            )

    conversation_uuid = str(uuid.uuid4())
    os.makedirs(f"server/users/{user_uuid}/conversations/{conversation_uuid}/", exist_ok=True)

    # Generate initial response
    response = client.chat.completions.create(
                messages=[
                    {"role": "system", "content": "You are a helpful assistant."},
                    {"role": "user", "content": starter_prompt}
                ],
                model=model,
                max_tokens=2048,
                stream=False,
            )
    
    with open(os.path.join("server", "users", user_uuid, "conversations", conversation_uuid, "conversation.json"), "w") as f:
        json.dump(
            {
                "messages": [
                    {"role": "system", "content": "You are a helpful assistant."},
                    {"role": "user", "content": starter_prompt},
                    {"role": "assistant", "content": response.choices[0].message.content}
                ]
            },
            f
        )

    return summary.choices[0].message.content, conversation_uuid, response.choices[0].message.content 


if __name__ == "__main__":
    user_uuid = "test-user-uuid"
    starter_prompt = "Explain the theory of relativity."
    summary, conversation_uuid, initial_response = start_conversation(user_uuid, starter_prompt)
    print("Summary:", summary)
    print("Conversation UUID:", conversation_uuid)
    print("Initial Response:", initial_response)