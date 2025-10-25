#---------------------------------------------------------------------------------------
# Time
#---------------------------------------------------------------------------------------
import time
#---------------------------------------------------------------------------------------

#---------------------------------------------------------------------------------------
# Simplify
#---------------------------------------------------------------------------------------
from server.simplify import *
#---------------------------------------------------------------------------------------


#---------------------------------------------------------------------------------------
# Gotify Notification
#---------------------------------------------------------------------------------------
gotify_active = True  # Set to False to disable Gotify notifications
try:
    from server.gotify_notif import send_message
    print("Gotify notification module loaded successfully.")
    send_message(
        title="Embedding Model",
        message="Embedding model initialization has begun.",
        priority=5
    )
except ImportError:
    gotify_active = False
    print("Gotify notification module not found. Skipping Gotify integration.")

#---------------------------------------------------------------------------------------
# FastAPI
#---------------------------------------------------------------------------------------
start = time.time()
import fastapi
print(f"Loaded FastAPI in {(time.time() - start) * 1000:.2f} milliseconds")

# uvicorn embedding_model:app --host 127.0.0.1 --port 8000 --log-level warning
#---------------------------------------------------------------------------------------

#---------------------------------------------------------------------------------------
# Numpy
#---------------------------------------------------------------------------------------
start = time.time()
import numpy as np
print(f"Loaded numpy in {(time.time() - start) * 1000:.2f} milliseconds")
#---------------------------------------------------------------------------------------

#---------------------------------------------------------------------------------------
# Pydantic
#---------------------------------------------------------------------------------------
start = time.time()
from pydantic import BaseModel
print(f"Loaded pydantic in {(time.time() - start) * 1000:.2f} milliseconds")
#---------------------------------------------------------------------------------------

#---------------------------------------------------------------------------------------
# Sentence Transformers
#---------------------------------------------------------------------------------------
start = time.time()
from sentence_transformers import SentenceTransformer
print(f"Loaded sentence_transformers in {(time.time() - start) * 1000:.2f} milliseconds")
#---------------------------------------------------------------------------------------

#---------------------------------------------------------------------------------------
# Embedding model initialization
#---------------------------------------------------------------------------------------
embedding_model = "BAAI/bge-small-en-v1.5"
start = time.time()
model = SentenceTransformer(embedding_model, device="cuda", cache_folder="C:/Models/embedding_models")
print(f"Initialized embedding model in {(time.time() - start) * 1000:.2f} milliseconds")
#---------------------------------------------------------------------------------------

if gotify_active:
    send_message(
        title="Embedding Model Initialized",
        message=f"Model {embedding_model} loaded successfully.",
        priority=5
    )

wait(0.25)
clear()
print("Done.")

class EmbedRequest(BaseModel):
    text: str

app = fastapi.FastAPI()

gotify_url = "tcp://100.124.29.36:8080/message"
gotify_token = "AOVOkVSMVK5Yyc6"

@app.post("/embed")
def query(request: EmbedRequest):
    """
    Endpoint to get the embedding of a text.
    """
    clear()
    print("Embedding text:", request.text)
    embedding = model.encode([request.text], convert_to_numpy=True)
    normalized = embedding / np.linalg.norm(embedding, axis=1, keepdims=True)
    return {'vector':normalized.tolist()[0]}