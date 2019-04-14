package com.lx.server.kafka.consumer;

import java.util.Optional;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaComsumer {
	@KafkaListener(topics = {"userLogin"})
    public  void  listen(ConsumerRecord<?,?> record){
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        Object message = kafkaMessage.get();
        System.out.println("消费消息:"+message);
    }
}
