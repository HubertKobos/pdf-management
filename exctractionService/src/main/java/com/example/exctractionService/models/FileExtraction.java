package com.example.exctractionService.models;

import lombok.Builder;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.UUID;

@RedisHash("FileExtraction")
@Builder
public class FileExtraction implements Serializable {
    private String id;
    private UUID requestId;
    private String extractedText;
}
