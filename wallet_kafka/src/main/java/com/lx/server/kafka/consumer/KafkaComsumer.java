package com.lx.server.kafka.consumer;

import java.util.Optional;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lx.server.kafka.bean.Message;

@Component
public class KafkaComsumer {
	
	@KafkaListener(topics = {"userTopic"})
    public void userTopicListen(ConsumerRecord<?,?> record){
        Optional<Message> kafkaMessage = (Optional<Message>) Optional.ofNullable(record.value());
        Object message = kafkaMessage.get();
        JSONObject jsonObject = JSON.parseObject(message.toString());
        Message info = new Message(jsonObject.getInteger("type"), jsonObject.getString("title"),jsonObject.get("data"));
        System.out.println(info);
    }
}
