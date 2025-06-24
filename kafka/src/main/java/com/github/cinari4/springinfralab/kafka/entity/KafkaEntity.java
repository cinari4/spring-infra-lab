package com.github.cinari4.springinfralab.kafka.entity;

public class KafkaEntity {
    private String id;
    private String message;

    public KafkaEntity() {
    }

    public KafkaEntity(String id, String message) {
        this.id = id;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "KafkaEntity{" +
                "id='" + id + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
