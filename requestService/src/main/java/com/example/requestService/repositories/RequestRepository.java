package com.example.requestService.repositories;

import com.example.requestService.models.RequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RequestRepository extends JpaRepository<RequestEntity, UUID> {
    Optional<RequestEntity> findByRequestId(UUID requestId);
}
