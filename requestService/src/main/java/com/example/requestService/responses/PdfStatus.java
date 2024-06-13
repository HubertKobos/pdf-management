package com.example.requestService.responses;

import com.example.requestService.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class PdfStatus {
    UUID requestId;
    RequestStatus requestStatus;
}
