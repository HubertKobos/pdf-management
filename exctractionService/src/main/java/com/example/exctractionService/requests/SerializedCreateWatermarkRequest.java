package com.example.exctractionService.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class SerializedCreateWatermarkRequest {
    private final UUID requestId;
    private final byte[] fileBytes;

    @JsonCreator
    public SerializedCreateWatermarkRequest(@JsonProperty("requestId") UUID requestId,
                                            @JsonProperty("fileBytes") byte[] fileBytes) {
        this.requestId = requestId;
        this.fileBytes = fileBytes;
    }
}
