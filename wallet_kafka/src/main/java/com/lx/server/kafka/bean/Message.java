package com.lx.server.kafka.bean;

public class Message {
    private  Integer id;
    private  String name;

    public Message(){}

    public Message(Integer id,String name){
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

}
