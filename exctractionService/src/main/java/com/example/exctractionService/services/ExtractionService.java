package com.example.exctractionService.services;

import com.example.exctractionService.CustomMultipartFile;
import com.example.exctractionService.models.FileExtraction;
import com.example.exctractionService.repositories.FileExtractionRepository;
import com.example.exctractionService.requests.SerializedCreateWatermarkRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExtractionService {
    private final FileExtractionRepository fileExtractionRepository;
    public void saveExtractedText(SerializedCreateWatermarkRequest request){

        byte[] fileBytes = request.getFileBytes();

        MultipartFile multipartFile = new CustomMultipartFile(
                fileBytes,
                request.getRequestId().toString(),
                "application/pdf"
        );

        try{
            String extractedText = extractText(multipartFile);
            FileExtraction fileExtraction = FileExtraction.builder()
                    .requestId(request.getRequestId())
                    .extractedText(extractedText)
                    .build();
            fileExtractionRepository.save(fileExtraction);
        }catch (IOException e){
            e.printStackTrace();
        }

    }
    private String extractText(MultipartFile multipartFile) throws IOException {
        try (InputStream inputStream = multipartFile.getInputStream();
             PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        }
    }
}
