package com.example.watermarkService.controllers;

import com.example.watermarkService.models.PdfDocument;
import com.example.watermarkService.repositories.PefDocumentRepository;
import com.example.watermarkService.requests.CreateNewPdfRequest;
import com.example.watermarkService.requests.CreateWatermarkRequest;
import com.example.watermarkService.responses.CreateNewDocumentResponse;
import com.example.watermarkService.services.WatermarkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.util.Matrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/watermark")
@Slf4j
@RequiredArgsConstructor
public class WaterMarkController {
    private final WatermarkService watermarkService;
    private final PefDocumentRepository pdfDocumentRepository;


    @GetMapping("/{requestId}")
    public ResponseEntity<InputStreamResource> getPdfDocument(@PathVariable UUID requestId){
        System.out.println("requestId = " + requestId.toString());
        Optional<PdfDocument> pdfDocumentOptional = pdfDocumentRepository.findByRequestId(requestId);

        if(pdfDocumentOptional.isPresent()){
            PdfDocument pdfDocument = pdfDocumentOptional.get();
            ByteArrayInputStream bis = new ByteArrayInputStream(pdfDocument.getContent());

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Content-Disposition", "inline; filename=" + pdfDocument.getFileName());
            return ResponseEntity.ok()
                    .headers(httpHeaders)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(bis));
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/new")
    public ResponseEntity<CreateNewDocumentResponse> createNewDocument(@RequestBody CreateNewPdfRequest request){
        log.info("CreateNewPdfRequest -> " + request.toString());

        byte[] generatedPdfArr = watermarkService.generatePdf(request.getCompanyName(), request.getContent());
        CreateNewDocumentResponse response = CreateNewDocumentResponse.builder().byteArr(generatedPdfArr).build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
