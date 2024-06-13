package com.example.watermarkService.repositories;

import com.example.watermarkService.models.PdfDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PefDocumentRepository extends JpaRepository<PdfDocument, UUID> {
    Optional<PdfDocument>findByRequestId(UUID requestId);
}
