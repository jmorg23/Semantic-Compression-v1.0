import json
import socket
import threading
from groq_api_key import key
from groq import Groq




# from server.Modules import get_conversations, get_uuid
from Modules.get_conversations import get_conversations
from Modules.start_conversation import start_conversation
from Modules.get_messages import get_messages
client = Groq(
    api_key=key
)



model = "llama-3.3-70b-versatile"

from time import sleep

class Client:
    id :str
    connection = None
    def __init__(self, connection, id):
        self.connection = connection
        self.id = id
        self.messages = []
        pass

    def send_message(self, message):
        connection = self.connection
        json_str = json.dumps(message.__dict__)
        # print(json_str)
        connection.sendall(json_str.encode())


    def loop(self):
        while True:
            connection = self.connection
            print("Waiting for data from client...")
            data = connection.recv(4096)
            if not data:
                break

            message = data.decode()
            print("Received:", message)
            message = json.loads(message)
            print("loaded json")
            try:

                input_message = InputMessage(**message)
                print("type is a input message")
            except Exception as e:
                # print("e: ",e)
                try:
                    print("not inputmessage")

                    input_message = ChangeConvo(**message)
                    connection.sendall(get_messages(input_message.uuid,input_message.cuuid))
                    print("type is a change convo")

                    #change model
                    continue
                except Exception as e:
                    print("not change convo")

                    input_message = CreateConvo(**message)
                    print("created it into a create convo")
                    summary, uuid, response = start_conversation(input_message.uuid, input_message.prompt)

                    response_bytes = json.dumps(ClientCreate(summary,  uuid, response).__dict__)
                    connection.sendall(response_bytes.encode())



                    # summary, uuid, response = start_conversation(input_message.uuid, input_message.prompt)
                    # connection.sendall(
                    #     {
                    #         'summary':summary, 
                    #         'uuid':uuid, 
                    #         'response':response
                    #     }
                    # ) 
                    print("type is a Create Convo")
               
                continue
            message = input_message.content
            timestamp = input_message.timestamp
            limit = input_message.limit

            self.messages.append({"role": "user", "content": message})


            response = client.chat.completions.create(
                messages=self.messages,
                model=model,
                max_tokens=2048,
                stream=True,
            )

            message = ''
            self.send_message(Message(message, 1))
            for chunk in response:
                if chunk.choices[0].delta.content:
                    message += chunk.choices[0].delta.content
                    token = chunk.choices[0].delta.content
                # print(Message(token.strip(), 0))
                    # self.send_message(Message(token.strip(), 0)) 
                    # print(chunk.choices[0].delta.content, end="", flush=True)
            
            self.send_message(Message(message, -1))
            # print(Message(message, -1).message)

            self.messages.append({"role": "assistant", "content": message})

            # self.send_message(Message("a", 1))
            # sleep(0.01)
            # self.send_message(Message("Message from server", 0))
            # sleep(0.01)
            # self.send_message(Message("Message from server", 0))
            # sleep(0.01)
            # self.send_message(Message("Message from server", 0))
            # sleep(0.01)
            # self.send_message(Message("Message from server", 0))
            # sleep(0.01) 
            # self.send_message(Message("Message from server", 0))
            # sleep(0.01) 
            # self.send_message(Message("Message from server", 0))
            # sleep(0.01)
            # self.send_message(Message("Message from server", -1))





class ServerMain:

    def __init__(self):
        self.start_server()




    def handle_connections(self, server_socket):
        while True:
            print("Waiting for a connection...")
            connection, client_address = server_socket.accept()
            try:
                print("Connection from", client_address)
                data = json.loads(connection.recv(4096).decode())
                client_info = ClientInfo(**data)
                
                
                print("id: "+client_info.id)

                # Checks for null id and assigns a new one if not
                # if(client_info.id == ""):

                #     client_info.id = get_uuid()
                #     #client_info.id = "boom"
                #     connection.sendall(client_info.id.encode())

                # else:
                #     connection.sendall(get_conversations(client_info.id))


                client = Client(connection,client_info.id)
                threading.Thread(target=client.loop()).start()
                
            except Exception as e:
                print("An error occurred:", e)
                connection.close()

    def start_server(self):
        # creates the server socket
        server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

        server_address = ('100.68.170.117', 24483)
        server_socket.bind(server_address)

        # Listen for incoming connections
        server_socket.listen(1)
        print("Server is listening on", server_address)
        self.handle_connections(server_socket)

class CreateConvo:
    prompt:str
    uuid:str
    def __init__(self, prompt, uuid):
        self.prompt = prompt
        self.uuid = uuid
        

class ChangeConvo:
    cuuid: str
    uuid: str
       
    def __init__(self, cuuid, uuid):
        self.cuuid = cuuid
        self.uuid = uuid 

class Message:
    message: str
    type: int    
    def __init__(self, content, type):
        self.message = content
        self.type = type

class ClientInfo:
    id: str
    def __init__(self, id):
        self.id = id


class ClientCreate:
    summary: str
    uuid: str
    response: str
    def __init__(self, summary, uuid, response):
        self.response = response
        self.uuid = uuid
        self.summary = summary
        

class InputMessage:
    content: str
    timestamp: int
    limit: int = 10
    def __init__(self, content, timestamp, limit=10):
        self.content = content
        self.timestamp = timestamp
        self.limit = limit


if __name__ == "__main__":
    ServerMain()