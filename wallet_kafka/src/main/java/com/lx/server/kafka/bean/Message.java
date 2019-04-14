package com.lx.server.kafka.bean;

import lombok.Data;

@Data
public class Message {
    private Integer type;
    private String title;
    private Object data;

    public Message(Integer type,String title,Object data){
        this.type = type;
        this.title = title;
        this.data = data;
    }
}
