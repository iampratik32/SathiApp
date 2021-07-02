package com.reddigitalentertainment.sathijivanko;

public class Chat {
    private String sender;
    private String receiver;
    private String message;
    private long messageTime;
    private String type;
    private String seen;
    private String key;

    public Chat(String sender, String receiver, String message, long messageTime, String type,String seen) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.messageTime = messageTime;
        this.type = type;
        this.seen = seen;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public Chat() {}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
