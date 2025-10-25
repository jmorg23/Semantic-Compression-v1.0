package com.ui;

import java.util.ArrayList;

import com.networking.Client;

public class Conversation {
    // {role:content}

    private ArrayList<Message> messages = new ArrayList<>();
    private String uuid;
    private String name;

    


    public String getID(){
        return uuid;
    }

    public ArrayList<Message> load(Client client){
        return messages = client.getMessages(uuid);

    }

    // public Conversation(ArrayList<recover) {
    //     messages = recover;

    // }   

    // public Conversation(String uuid, String input) {
    //     /*
    //      * Send to python:
    //      * uuid
    //      * input
    //      */


    // }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Conversation(String uuid, String name){
        this.uuid = uuid;
        this.name = name;
    }

    public void addMessage(String role, String message){
        messages.add(new Message(role, message));
    }


    public class Message{
        private String role;
        private String content;
        public String getRole() {
            return role;
        }
        public Message() {
        }
        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
        public void setRole(String role) {
            this.role = role;
        }
        public String getContent() {
            return content;
        }
        public void setContent(String content) {
            this.content = content;
        }

    }




}
