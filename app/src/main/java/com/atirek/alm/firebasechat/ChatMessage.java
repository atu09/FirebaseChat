package com.atirek.alm.firebasechat;

/**
 * Created by Alm on 8/16/2016.
 */
public class ChatMessage {

    String msg;
    String user;

    public ChatMessage(String msg, String user) {
        this.msg = msg;
        this.user = user;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
