package com.example.watermarkService.models;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class PdfDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UUID requestId;

    private String fileName;

    @Lob
    private byte[] content;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
    public void setRequestId(UUID requestId){
        this.requestId = requestId;
    }
    public UUID getRequestId(){
        return this.requestId;
    }
}
