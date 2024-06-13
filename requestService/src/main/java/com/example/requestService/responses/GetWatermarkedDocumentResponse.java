package com.example.requestService.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetWatermarkedDocumentResponse {
    private Long id;
    private UUID requestId;
    private String fileName;
    private byte[] content;
}
