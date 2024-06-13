package com.example.requestService.services;

import com.example.requestService.config.RabbitMqConfig;
import com.example.requestService.enums.RabbitMqQueue;
import com.example.requestService.requests.SerializedCreateWatermarkRequest;
import com.example.requestService.responses.PdfStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMQReceiver {
    private final RequestService requestService;

    @RabbitListener(queues = RabbitMqConfig.WATERMARK_QUEUE)
    public void receiveStatus(PdfStatus status) {
        requestService.changeStatus(status);
    }
}
