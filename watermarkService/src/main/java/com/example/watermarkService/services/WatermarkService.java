package com.example.watermarkService.services;

import com.example.watermarkService.CustomMultipartFile;
import com.example.watermarkService.models.PdfDocument;
import com.example.watermarkService.repositories.PefDocumentRepository;
import com.example.watermarkService.requests.SerializedCreateWatermarkRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class WatermarkService {
    private final PefDocumentRepository pdfDocumentRepository;
    private final RabbitMQSender rabbitMQSender;

    public byte[] generatePdf(String companyName, String content) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Set font and size
                contentStream.setFont(PDType1Font.HELVETICA, 12);

                // Date in the top-left corner
                String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 750); // Coordinates for top-left corner
                contentStream.showText(date);
                contentStream.endText();

                // Company name in the top-right corner
                contentStream.beginText();
                contentStream.newLineAtOffset(450, 750); // Coordinates for top-right corner
                contentStream.showText(companyName);
                contentStream.endText();

                // Content in the center
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 400); // Coordinates for the center
                contentStream.setLeading(14.5f); // Line spacing
                String[] lines = content.split("\n");
                for (String line : lines) {
                    contentStream.showText(line);
                    contentStream.newLine();
                }
                contentStream.endText();
            } catch (IOException e) {
                e.printStackTrace();
            }

            document.save(outputStream); // Save document to the output stream
            document.close();

            // Return the byte array of the PDF content
            byte[] byteArray = outputStream.toByteArray();
            log.info("array -> " + Arrays.toString(byteArray));
            return byteArray;
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Handle error appropriately
        }
    }

    public void addWatermark(SerializedCreateWatermarkRequest request){
        byte[] fileBytes = request.getFileBytes();
        MultipartFile file = new CustomMultipartFile(
                fileBytes,
                request.getRequestId().toString(),
                "application/pdf"
        );
        UUID requestId = request.getRequestId();
        try {

            Path tempFilePath = Files.createTempFile("uploaded-", ".pdf");
            Files.copy(file.getInputStream(), tempFilePath, StandardCopyOption.REPLACE_EXISTING);

            Path watermarkedFilePath = addWatermark(tempFilePath.toString());

            saveWatermarkedPdfToDatabase(file.getOriginalFilename(), watermarkedFilePath, requestId);
            rabbitMQSender.watermarkedPdfChangeStatusToReady(requestId);

            Files.deleteIfExists(tempFilePath);
            Files.deleteIfExists(watermarkedFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveWatermarkedPdfToDatabase(String originalFilename, Path watermarkedFilePath, UUID requestId) throws IOException {
        PdfDocument pdfDocument = new PdfDocument();
        pdfDocument.setFileName("watermarked-" + originalFilename);
        pdfDocument.setRequestId(requestId);
        pdfDocument.setContent(Files.readAllBytes(watermarkedFilePath));

        pdfDocumentRepository.save(pdfDocument);
    }

    private Path addWatermark(String inputPath) throws IOException {
        Path outputPath = Files.createTempFile("watermarked-", ".pdf");

        try (PDDocument document = PDDocument.load(new File(inputPath))) {
            for (PDPage page : document.getPages()) {
                try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {

                    // Draw a red circle at the center of the page
                    contentStream.setNonStrokingColor(Color.RED);
                    float cx = 400;
                    float cy = 400;
                    float r = 80;

                    contentStream.setLineWidth(3f);

                    final float k = 0.552284749831f;
                    contentStream.moveTo(cx - r, cy);
                    contentStream.curveTo(cx - r, cy + k * r, cx - k * r, cy + r, cx, cy + r);
                    contentStream.curveTo(cx + k * r, cy + r, cx + r, cy + k * r, cx + r, cy);
                    contentStream.curveTo(cx + r, cy - k * r, cx + k * r, cy - r, cx, cy - r);
                    contentStream.curveTo(cx - k * r, cy - r, cx - r, cy - k * r, cx - r, cy);
                    contentStream.stroke();

                    // Write "Approved" inside the circle
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 30);
                    contentStream.setNonStrokingColor(Color.RED);
                    contentStream.newLineAtOffset(cx - 70, cy - 15); // Adjust the position as needed
                    contentStream.showText("Approved");
                    contentStream.endText();
                }
            }
            document.save(outputPath.toFile());
        }

        return outputPath;
    }

}
