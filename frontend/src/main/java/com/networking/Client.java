package com.networking;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ui.Conversation;
import com.ui.Panel;

public class Client {

    public static final int PORT = 24483;
    public static final String IP_ADDRESS = "100.68.170.117";

    public Socket socket;

    public BufferedInputStream is;
    public BufferedOutputStream os;

    public ObjectMapper mapper = new ObjectMapper();

    public ClientInfo myInfo;
    private String uuid;

    public Client() {

        myInfo = new ClientInfo("");
        try {
            System.out.println("attempting to connect to server at " + IP_ADDRESS + " on port " + PORT);
            socket = new Socket(IP_ADDRESS, PORT);
            os = new BufferedOutputStream(socket.getOutputStream());
            is = new BufferedInputStream(socket.getInputStream());
            System.out.println("connected to server!");
            System.out.println("sending UUID: ");
            FileInputStream fis = new FileInputStream(getClass().getResource("/uuid.txt").getPath());
            String id = new String(fis.readAllBytes());
            myInfo.setId(id.trim());
            uuid = id;
            fis.close();

            System.out.println("UUID: " + myInfo.getId());

            os.write(mapper.writeValueAsString(myInfo).getBytes());
            os.flush();
            if (id.equals("")) {
                System.out.println("UUID is empty! will wait for new one");
                byte[] buffer = new byte[1024];
                is.read(buffer);
                id = new String(buffer).trim();
                myInfo.setId(id);
                uuid = id;

                System.out.println("received new UUID from server: " + myInfo.getId());
                System.out.println("saving UUID to uuid.txt");
                FileOutputStream fout = new FileOutputStream(getClass().getResource("uuid.txt").getPath());
                fout.write(id.getBytes());
                fout.flush();
                fout.close();
            } else {
                // byte[] buffer = new byte[1024];
                // is.read(buffer);
                // Conversations convos = mapper.readValue( new String(buffer).trim(),
                // Conversations.class);
                // Panel.addConvos(convos.conversationMap);

            }
            System.out.println("connection done!");


        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }

    }


    public String getUuid() {
        return uuid;
    }

    public com.ui.Response newConversation(String userIn) {
        ConversationStarter convo = new ConversationStarter();
        convo.setUuid(uuid);
        convo.setPrompt(userIn);
        System.out.println("setting uuid to: "+uuid);
                System.out.println("setting prompt to: "+userIn);

        try {
            os.write(mapper.writeValueAsString(convo).getBytes());
            os.flush();

            byte[] buffer = new byte[1024];
            is.read(buffer);
            ConvoStarter cs = mapper.readValue(new String(buffer), ConvoStarter.class);
            System.out.println("uid: "+cs.uuid+" name: "+cs.summary);

            Panel.currentConversation = new Conversation(cs.uuid, cs.summary);
            return new com.ui.Response(cs.response, Panel.nexty, true);

        } catch (IOException e) {
            
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }

    public String receiveMessages() {

        while (true) {

            try {
                byte[] buffer = new byte[4096];

                is.read(buffer);
                String received = new String(buffer).trim();
                System.out.println("rec: " + received);
                
                Packet packet = mapper.readValue(received, Packet.class);
                System.out.println("received message: " + packet.getMessage() + " of type " + packet.getType());

                return packet.getMessage();
                // if(packet.getType() == -1) {
                // System.out.println("done");
                // break;
                // }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void sendMessage(String message) {
        Message msg = new Message("" + System.currentTimeMillis(), message, 10);
        try {
            os.write(mapper.writeValueAsString(msg).getBytes());
            os.flush();
            receiveMessages();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<com.ui.Conversation.Message> getMessages(String cuuid){
        try{
            os.write(mapper.writeValueAsString(new ChangeConvo(cuuid, uuid)).getBytes());
            os.flush();

            byte[] buffer = new byte[4096];
            int read = is.read(buffer);
            if (read == -1) {
                return null;
            }
            String json = new String(buffer, 0, read).trim();
            System.out.println(json);
            return mapper.readValue(json, mapper.getTypeFactory()
                    .constructCollectionType(ArrayList.class, com.ui.Conversation.Message.class));
        }catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
            System.exit(0);

        }
        return null;
    }
    public static class ConvoStarter {
        private String summary;
        private String response;
        private String uuid;
        public String getSummary() {
            return summary;
        }
        public void setSummary(String name) {
            this.summary = name;
        }
        public ConvoStarter() {
        }
        public ConvoStarter(String name, String response, String uuid) {
            this.summary = name;
            this.response = response;
            this.uuid = uuid;
        }
        public String getResponse() {
            return response;
        }
        public void setResponse(String response) {
            this.response = response;
        }
        public String getUuid() {
            return uuid;
        }
        public void setUuid(String uuid) {
            this.uuid = uuid;
        }
        
    }
    
    public class Response {
        private String role;
        private String content;
        private int type;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public Response() {
        }

        public Response(String role, String content, int type) {
            this.role = role;
            this.content = content;
            this.type = type;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

    }

    public static class ClientInfo {

        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public ClientInfo(String id) {
            this.id = id;
        }

    }

    public static class Packet {

        private String message;
        private int type;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public Packet() {

        }

    }
    public static class ChangeConvo {
        private String cuuid;
        private String uuid;

        public ChangeConvo(String cuuid, String uuid) {
            this.cuuid = cuuid;
            this.uuid = uuid;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getCuuid() {
            return cuuid;
        }

        public ChangeConvo() {
        }

        public void setCuuid(String cuuid) {
            this.cuuid = cuuid;
        }
        
        
    }

    public static class Message {
        private String timestamp;
        private String content;
        private int limit = 10;

        public Message() {
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public Message(String timestamp, String content, int limit) {
            this.timestamp = timestamp;
            this.content = content;
            this.limit = limit;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }
    }



    public class ConversationStarter {
        private String uuid;
        private String prompt;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getPrompt() {
            return prompt;
        }

        public ConversationStarter() {
        }

        public void setPrompt(String prompt) {
            this.prompt = prompt;
        }

        public ConversationStarter(String uuid, String prompt) {
            this.uuid = uuid;
            this.prompt = prompt;
        }

    }
}
