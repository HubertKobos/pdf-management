package com.example.requestService.models;

import com.example.requestService.enums.RequestStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RequestEntity {
    @Id
    @GeneratedValue
    private UUID id;
    private UUID requestId;
    private RequestStatus status;


}
