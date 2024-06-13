package com.example.requestService;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.UUID;

public class ByteToMultipartFileConverter {

    public static ResponseEntity<InputStreamResource> convertToMultipartFile(byte[] byteArray) {
        try {
            // Convert byte array to ByteArrayInputStream
            InputStream inputStream = new ByteArrayInputStream(byteArray);

            // Create a custom implementation of MultipartFile
            MultipartFile multipartFile = new MultipartFile() {
                @Override
                public String getName() {
                    return UUID.randomUUID().toString();
                }

                @Override
                public String getOriginalFilename() {
                    return "generated.pdf";
                }

                @Override
                public String getContentType() {
                    return MediaType.APPLICATION_PDF_VALUE;
                }

                @Override
                public boolean isEmpty() {
                    return byteArray == null || byteArray.length == 0;
                }

                @Override
                public long getSize() {
                    return byteArray.length;
                }

                @Override
                public byte[] getBytes() throws IOException {
                    return byteArray;
                }

                @Override
                public InputStream getInputStream() throws IOException {
                    return new ByteArrayInputStream(byteArray);
                }

                @Override
                public void transferTo(File dest) throws IOException, IllegalStateException {
                    Files.write(dest.toPath(), byteArray);
                }
            };


            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + multipartFile.getOriginalFilename());


            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(multipartFile.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

;