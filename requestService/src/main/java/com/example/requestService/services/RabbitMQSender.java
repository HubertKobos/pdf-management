package com.example.requestService.services;

import com.example.requestService.config.RabbitMqConfig;
import com.example.requestService.enums.RabbitMqQueue;
import com.example.requestService.requests.CreateNewPdfRequest;
import com.example.requestService.requests.CreateWatermarkRequest;
import com.example.requestService.requests.SerializedCreateWatermarkRequest;
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
    public void sendNewDocumentQueue(CreateNewPdfRequest request) {
        rabbitTemplate.convertAndSend(RabbitMqQueue.CREATE_NEW_DOCUMENT_QUEUE.getDescription(), request);
    }
    public void sendPdfDocumentQueue(CreateWatermarkRequest request, UUID requestId){
//        log.info("sender -->" + request.toString());
        try{
            // Convert MultipartFile to byte array
            byte[] pdfBytes = request.getFile().getBytes();

            SerializedCreateWatermarkRequest serializedRequest = SerializedCreateWatermarkRequest.builder()
                    .requestId(requestId)
                    .fileBytes(pdfBytes)
                    .build();
//            log.info("serializedRequest -> " + serializedRequest.toString());
            rabbitTemplate.convertAndSend(RabbitMqQueue.DOCUMENT_QUEUE.getDescription(), serializedRequest);

        }catch (IOException ex){
            log.error("Error occurred while converting file to bye array: " + ex.getMessage());
        }
    }
}
