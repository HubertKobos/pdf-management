package com.example.watermarkService.services;

import com.example.watermarkService.config.RabbitMQConfig;
import com.example.watermarkService.requests.CreateNewPdfRequest;
import com.example.watermarkService.requests.CreateWatermarkRequest;
import com.example.watermarkService.requests.SerializedCreateWatermarkRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMQReceiver {
    private final WatermarkService watermarkService;
//    @RabbitListener(queues = RabbitMQConfig.CREATE_NEW_DOCUMENT_QUEUE)
//    public void receiveNewPdfRequest(CreateNewPdfRequest request) {
//        System.out.println("Received message: " + request.toString());
//        // Process the message as needed
//    }

    @RabbitListener(queues = RabbitMQConfig.DOCUMENT_QUEUE)
    public void receivePdf(SerializedCreateWatermarkRequest request) {

        watermarkService.addWatermark(request);

    }

}
