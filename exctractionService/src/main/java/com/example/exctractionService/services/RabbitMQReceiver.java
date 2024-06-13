package com.example.exctractionService.services;

import com.example.exctractionService.config.RabbitMQConfig;
import com.example.exctractionService.requests.SerializedCreateWatermarkRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMQReceiver {
    private final ExtractionService extractionService;

    @RabbitListener(queues = RabbitMQConfig.DOCUMENT_QUEUE)
    public void receive(SerializedCreateWatermarkRequest request) {
        extractionService.saveExtractedText(request);

    }
}
