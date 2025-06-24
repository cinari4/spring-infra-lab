package com.github.cinari4.springinfralab.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = {
    "spring.kafka.bootstrap-servers=localhost:19092",
    "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
    "spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer"
})
public class LocalKafkaBasicTest {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final String SERVER_CONFIG = "localhost:19092";

    /**
     * KafkaTemplate을 사용해 메시지를 전송하고,
     * 로컬 Kafka 서버에서 소비하여 전송/수신 기능을 검증
     */
    @Test
    public void testSendReceiveLocalKafka() {
        // 1) 테스트용 토픽, 키, 값 정의
        String topic = "test-topic";
        String key = "testKey:" + UUID.randomUUID();
        String value = "testValue:" + UUID.randomUUID();
        String testGroup = "testGroup-" + UUID.randomUUID();

        // 2) CONSUMER SETUP: 로컬 Kafka 서버 접속 정보 및 직렬화 설정
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,  SERVER_CONFIG);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, testGroup);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);

        // 3) CREATE CONSUMER: KafkaConsumer 인스턴스 생성 및 토픽 구독
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(singletonList(topic));

        // 파티션 할당 유도 (컨슈머가 토픽에 할당될 때까지 poll)
        consumer.poll(Duration.ofMillis(2000));

        // 4) PRODUCE: KafkaTemplate로 메시지 전송
        kafkaTemplate.send(topic, key, value);
        kafkaTemplate.flush(); // 즉시 전송 보장

        ConsumerRecords<String, String> records = ConsumerRecords.empty();
        int attempts = 0;
        while (attempts < 10 && records.isEmpty()) { // 최대 10초간 시도
            records = consumer.poll(Duration.ofSeconds(1));
            attempts++;
        }

        ConsumerRecord<String, String> record = records.iterator().next();

        // 5) ASSERT: 전송한 메시지 1건 수신, 키/값 일치 검증
        assertNotNull(record);
        assertEquals(key, record.key(), "Key should match the sent key");
        assertEquals(value, record.value(), "Value should match the sent value");

        // 6) CLEANUP: consumer 종료
        consumer.close();
    }

}
