from embeddding_function import embed


class Message:
    def __init__(self, role:str, content:str, timestamp:int, limit:int=10):
        self.role = role
        self.content = content
        self.timestamp = timestamp
        self.limit = limit
        self.position = embed(self.content)

if __name__ == "__main__":
    message = Message("user", "Hello, world!", 1234567890)
    print(message.role)
    print(message.content)
    print(len(message.position), message.position[100])