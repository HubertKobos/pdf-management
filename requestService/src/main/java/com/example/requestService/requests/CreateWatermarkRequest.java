package com.example.requestService.requests;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
public class CreateWatermarkRequest {
//    private UUID requestId;
    MultipartFile file;
}
