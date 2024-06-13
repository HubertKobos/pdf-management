package com.example.watermarkService.services;


import com.example.watermarkService.enums.RabbitMqQueue;
import com.example.watermarkService.enums.RequestStatus;
import com.example.watermarkService.models.PdfDocument;
import com.example.watermarkService.requests.PdfStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMQSender {
    private final RabbitTemplate rabbitTemplate;

    public void watermarkedPdfChangeStatusToReady(UUID requestId){
        PdfStatus pdfStatus = PdfStatus.builder()
                        .requestId(requestId)
                                .requestStatus(RequestStatus.READY)
                                        .build();
        rabbitTemplate.convertAndSend(RabbitMqQueue.WATERMARK_QUEUE.getDescription(), pdfStatus);


    }
}