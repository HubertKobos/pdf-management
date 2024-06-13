package com.example.exctractionService.repositories;

import com.example.exctractionService.models.FileExtraction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileExtractionRepository extends CrudRepository<FileExtraction, String> {
}
