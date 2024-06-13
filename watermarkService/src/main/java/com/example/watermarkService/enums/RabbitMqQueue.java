package com.example.watermarkService.enums;

public enum RabbitMqQueue {
    WATERMARK_QUEUE("waterMarkQueue");
    private final String description;
    RabbitMqQueue(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
