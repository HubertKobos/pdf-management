package com.example.requestService.enums;

public enum RabbitMqQueue {
    CREATE_NEW_DOCUMENT_QUEUE("newDocumentQueue"),
    DOCUMENT_QUEUE("documentQueue"),
    WATERMARK_QUEUE("waterMarkQueue");

    private final String description;
    RabbitMqQueue(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
