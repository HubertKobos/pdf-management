package com.example.watermarkService.responses;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateNewDocumentResponse {
    byte[] byteArr;
}
