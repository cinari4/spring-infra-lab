package com.github.cinari4.springinfralab.kafka.kafka;

import com.github.cinari4.springinfralab.kafka.entity.KafkaEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class KafkaProducerCluster {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducerCluster.class);

    private final KafkaTemplate<String, KafkaEntity> kafkaTemplate;
    private final String topicName;

    public KafkaProducerCluster(KafkaTemplate<String, KafkaEntity> kafkaTemplate, @Value("${spring.kafka.template.default-topic}") String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    public void sendMessage(KafkaEntity kafkaEntity) {
        Message<KafkaEntity> message = MessageBuilder
                .withPayload(kafkaEntity)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .build();
        CompletableFuture<SendResult<String, KafkaEntity>> future = kafkaTemplate.send(message);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("producer: success >>> message: {} at offset {}", kafkaEntity, result.getRecordMetadata().offset());
            } else {
                log.error("producer: failure >>> message: {} error: {}", kafkaEntity, ex.getMessage());
            }
        });
    }
}
