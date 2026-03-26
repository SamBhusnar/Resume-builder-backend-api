package com.reer.resumebuilder.resume_builder_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {
    private List<String> message;
    private String status;
    private long timeStamp;
}
