package com.example.requestService.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

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
