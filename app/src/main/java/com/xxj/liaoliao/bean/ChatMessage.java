package com.xxj.liaoliao.bean;

/**
 * Created by Administrator on 15-4-24.
 */

/**
 * 用于描述聊天的消息
 */
public class ChatMessage {
    /**
     * 发出去的
     */
    public static final int SOURCE_TYPE_SEND=0;
    /**
     * 收到的
     */
    public static final int SOURCE_TYPE_RECEIVED=1;
    /**
     * 发消息的人
     */
    private String from;
    /**
     * 消息发给谁
     */
    private  String to;
    /**
     * 消息内容
     */
    private String body;
    /**
     * 接收/发送的事件
     */
    private long time;
    /**
     * 消息的来源类型，代表是发出去的还是收到的
     * 可选值 0：发出去的  1：收到的
     */
    private int sourcetype;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getSourcetype() {
        return sourcetype;
    }

    public void setSourcetype(int sourcetype) {
        this.sourcetype = sourcetype;
    }
}
