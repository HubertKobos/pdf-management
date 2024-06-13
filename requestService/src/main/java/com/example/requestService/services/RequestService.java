package com.example.requestService.services;

import com.example.requestService.ByteArrayMultipartFile;
import com.example.requestService.ByteToMultipartFileConverter;
import com.example.requestService.enums.RequestStatus;
import com.example.requestService.models.RequestEntity;
import com.example.requestService.repositories.RequestRepository;
import com.example.requestService.requests.CreateNewPdfRequest;
import com.example.requestService.requests.CreateWatermarkRequest;
import com.example.requestService.responses.CreateNewDocumentResponse;
import com.example.requestService.responses.GetWatermarkedDocumentResponse;
import com.example.requestService.responses.PdfStatus;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;
    private final RabbitMQSender rabbitMQSender;
    private final WebClient.Builder webClientBuilder;
    private final DiscoveryClient discoveryClient;
    public Mono<byte[]> createNewRequest(CreateNewPdfRequest request) {
        return createNewDocumentWatermarkServiceCall(request)
                .flatMap(responseBody -> {
                    log.info("Response from other service: " + Arrays.toString(responseBody.getByteArr()));
                    return Mono.just(responseBody.getByteArr());
                })
                .onErrorResume(error -> {
                    log.error("Error occurred: " + error.getMessage());
                    return Mono.error(error);
                });
    }


    public RequestEntity watermarkNewRequest(CreateWatermarkRequest request){
        UUID requestId = UUID.randomUUID();
        RequestEntity entity = RequestEntity.builder()
                .requestId(requestId)
                .status(RequestStatus.PENDING)
                .build();
        rabbitMQSender.sendPdfDocumentQueue(request, requestId);
        return requestRepository.save(entity);
    }
    public Mono<CreateNewDocumentResponse> createNewDocumentWatermarkServiceCall(CreateNewPdfRequest entity) {
        log.info("Request entity -> " + entity.toString());
        String serviceName = "watermark-service";
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
//        if (instances.isEmpty()) {
//            return Mono.error(new RuntimeException("Service not found: " + serviceName));
//        }

        ServiceInstance instance = instances.get(0);
        String baseUrl = instance.getUri().toString();
        String url = baseUrl + "/api/watermark/new";
        log.info("URL -> " + url);

        return webClientBuilder.build()
                .post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(entity))
                .retrieve()
                .bodyToMono(CreateNewDocumentResponse.class);

    }

    public void changeStatus(PdfStatus status){
        Optional<RequestEntity> byRequestId = requestRepository.findByRequestId(status.getRequestId());
        if(byRequestId.isPresent()){
            byRequestId.get().setStatus(status.getRequestStatus());
            requestRepository.save(byRequestId.get());
        }
    }

    public Boolean isWatermarkDocumentReady(UUID requestId) {
        Optional<RequestEntity> byRequestIdOptional = requestRepository.findByRequestId(requestId);
        return byRequestIdOptional.isPresent() && byRequestIdOptional.get().getStatus().equals(RequestStatus.READY);
    }

    public Optional<RequestEntity> getRequestEntityOptional(UUID requestId){
        return requestRepository.findByRequestId(requestId);
    }

    public Mono<MultipartFile> getWatermarkedDocument(UUID requestId) {
        String serviceName = "watermark-service";
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
        if (instances.isEmpty()) {
            return Mono.error(new RuntimeException("Service not found: " + serviceName));
        }
        ServiceInstance instance = instances.get(0);

        String baseUrl = instance.getUri().toString();
        String url = baseUrl + "/api/watermark/" + requestId;

        int maxInMemorySize = 16 * 1024 * 1024; // 16 MB

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(maxInMemorySize))
                .build();

        return webClientBuilder.exchangeStrategies(strategies).build()
                .get()
                .uri(url)
                .retrieve()
                .toEntity(byte[].class)
                .flatMap(responseEntity -> {
                    byte[] fileContent = responseEntity.getBody();
                    HttpHeaders headers = responseEntity.getHeaders();

                    // Detailed logging
                    System.out.println("Received status code: " + responseEntity.getStatusCode());
                    System.out.println("Received headers: " + headers);
                    System.out.println("Received content disposition: " + headers.getFirst("Content-Disposition"));
                    System.out.println("Received content type: " + headers.getContentType());
                    System.out.println("Received file content size: " + (fileContent != null ? fileContent.length : "null"));

                    // Create MultipartFile
                    MultipartFile multipartFile = null;
                    // Assuming the content disposition header provides the filename
                    String filename = extractFilename(headers.getFirst("Content-Disposition"));
                    String contentType = headers.getContentType().toString();
                    multipartFile = new ByteArrayMultipartFile(fileContent, "file", filename, contentType);

                    return Mono.just(multipartFile);
                })
                .doOnError(error -> System.err.println("Error occurred: " + error.getMessage()));
    }


    private String extractFilename(String contentDisposition) {
        if (StringUtils.isEmpty(contentDisposition)) {
            return "unknown.pdf";
        }
        String[] parts = contentDisposition.split(";");
        for (String part : parts) {
            if (part.trim().startsWith("filename=")) {
                return part.substring("filename=".length()).trim().replace("\"", "");
            }
        }
        return "unknown.pdf";
    }

}
