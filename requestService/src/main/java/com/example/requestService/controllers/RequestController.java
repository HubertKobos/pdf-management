package com.example.requestService.controllers;

import com.example.requestService.ByteToMultipartFileConverter;
import com.example.requestService.models.RequestEntity;
import com.example.requestService.requests.CreateNewPdfRequest;
import com.example.requestService.requests.CreateWatermarkRequest;
import com.example.requestService.services.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("/api/request")
@Log
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @PostMapping("/new")
    public Mono<ResponseEntity<InputStreamResource>> createNewDocument(@RequestBody CreateNewPdfRequest request){
        Mono<byte[]> newRequestMono = requestService.createNewRequest(request);

        return newRequestMono.map(ByteToMultipartFileConverter::convertToMultipartFile);

    }
    @PostMapping("/pdf")
    public ResponseEntity<RequestEntity> watermarkPdf(CreateWatermarkRequest request){
        RequestEntity createdEntity = requestService.watermarkNewRequest(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdEntity);
    }

    @GetMapping("/pdf/{requestId}")
    public Mono<ResponseEntity<?>> getPdfDocument(@PathVariable UUID requestId) {
        return requestService.getWatermarkedDocument(requestId)
                .map(multipartFile -> {
                    try {
                        InputStreamResource resource = new InputStreamResource(multipartFile.getInputStream());
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentDispositionFormData("attachment", multipartFile.getName());
                        headers.setContentType(MediaType.APPLICATION_PDF);
                        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
                    } catch (IOException e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                })
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }

    @GetMapping("/pdf/status/{requestId}")
    public ResponseEntity<RequestEntity> getPdfDocumentStatus(@PathVariable UUID requestId){
        Optional<RequestEntity> requestEntityOptional = requestService.getRequestEntityOptional(requestId);
        if(requestEntityOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(requestEntityOptional.get());
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        }
    }
}
