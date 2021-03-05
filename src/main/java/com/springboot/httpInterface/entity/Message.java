package com.springboot.httpInterface.entity;

/**
 * Created by yzn00 on 2019/6/24.
 */
public class Message {
    private  String channel;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    private  IMClient Sender;
    private CommonMessage message;

    public IMClient getSender() {
        return Sender;
    }

    public void setSender(IMClient sender) {
        Sender = sender;
    }

    public CommonMessage getMessage() {
        return message;
    }

    public void setMessage(CommonMessage message) {
        this.message = message;
    }
}
