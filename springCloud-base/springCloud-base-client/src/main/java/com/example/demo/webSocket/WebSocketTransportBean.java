package com.example.demo.webSocket;

import lombok.Data;

import java.util.Date;

@Data
public class WebSocketTransportBean {

    private String sessionId;//Session Id
    private String chatId;//房间号
    private Date createdTime;//创建时间
    private String msg;//消息

    private String fromAccountId;//发起人-帐号
    private String fromIp;//发起人-IP
    private String fromNickName;//发起人-昵称

    private String toAccountId;//接收人-帐号
    private String toIp;//接收人-IP
    private String toNickName;//接收人-昵称

}
