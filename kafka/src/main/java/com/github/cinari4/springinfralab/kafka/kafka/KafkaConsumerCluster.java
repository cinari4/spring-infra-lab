package com.github.cinari4.springinfralab.kafka.kafka;

import com.github.cinari4.springinfralab.kafka.entity.KafkaEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumerCluster {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerCluster.class);

    @KafkaListener(topics = "${spring.kafka.template.default-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(@Payload KafkaEntity message, @Headers MessageHeaders headers) {
        log.info("consumer: success >>> message: {} headers: {}", message, headers);
    }
}
