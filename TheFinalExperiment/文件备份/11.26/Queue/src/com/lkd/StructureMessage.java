package com.lkd;

public class StructureMessage {

    private String messageID=null;
    private String Tag=null;
    private String data=null;

    public StructureMessage(String  messageID, String tag, String data) {
        this.messageID = messageID;
        this.Tag = tag;
        this.data = data;
    }

    public String getmessageID() {
        return messageID;
    }

    public String getTag() {
        return Tag;
    }

    public String getData() {
        return data;
    }
}
