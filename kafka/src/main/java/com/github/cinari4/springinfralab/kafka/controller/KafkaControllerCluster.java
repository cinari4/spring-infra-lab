package com.github.cinari4.springinfralab.kafka.controller;

import com.github.cinari4.springinfralab.kafka.entity.KafkaEntity;
import com.github.cinari4.springinfralab.kafka.kafka.KafkaProducerCluster;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KafkaControllerCluster {

    private final KafkaProducerCluster producer;

    public KafkaControllerCluster(KafkaProducerCluster producer) {
        this.producer = producer;
    }

    @PostMapping("/kafka/produce/cluster")
    public String sendMessage(@RequestBody KafkaEntity message) {
        producer.sendMessage(message);
        return "ok";
    }
}
