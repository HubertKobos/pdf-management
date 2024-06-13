package com.example.watermarkService.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
public class CreateNewPdfRequest {
    private String content;
    private String date;
    private String companyName;

    @JsonCreator
    public CreateNewPdfRequest(
            @JsonProperty("content") String content,
            @JsonProperty("date") String date,
            @JsonProperty("companyName") String companyName) {
        this.content = content;
        this.date = date;
        this.companyName = companyName;
    }
}
