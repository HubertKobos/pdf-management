package com.example.watermarkService.requests;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Data
public class CreateWatermarkRequest {
    private UUID requestId;
    MultipartFile file;
}
