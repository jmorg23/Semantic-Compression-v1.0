from message_class import Message

class conversation:
    def __init__(self, user_id:str, conversation_id:str, messages:list[Message]|list[dict]|str|None=None):

        '''
        :param user_id: The ID of the user.
        :param conversation_id: The ID of the conversation.
        :param messages: A list of messages in the conversation. Can be a list of Message objects, a raw message metadata dictionary, a filepath, or None if you're crazy like that.
        '''
        self.user_id = user_id
        self.conversation_id = conversation_id
        if isinstance(messages, str):
            with open(messages, 'r') as f:
                lines = f.readlines()
            self.messages = [Message("user", line.strip(), idx) for idx, line in enumerate(lines)]
        elif isinstance(messages, list):
            if all(isinstance(msg, dict) for msg in messages):
                self.messages = [Message(**msg) for msg in messages]
            elif all(isinstance(msg, Message) for msg in messages):
                self.messages = messages
        elif messages is None:
            self.messages = []
        else:
            self.messages = messages
