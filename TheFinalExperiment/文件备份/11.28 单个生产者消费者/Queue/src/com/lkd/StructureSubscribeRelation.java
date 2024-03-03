package com.lkd;

public class StructureSubscribeRelation {
    private String consumerID=null;
    private String consumerTag=null;

    public StructureSubscribeRelation(String consumerID, String consumerTag) {
        this.consumerID = consumerID;
        this.consumerTag = consumerTag;
    }

    public String getConsumerID() {
        return consumerID;
    }

    public String getConsumerTag() {
        return consumerTag;
    }

    public void setConsumerID(String consumerID) {
        this.consumerID = consumerID;
    }

    public void setConsumerTag(String consumerTag) {
        this.consumerTag = consumerTag;
    }
}
