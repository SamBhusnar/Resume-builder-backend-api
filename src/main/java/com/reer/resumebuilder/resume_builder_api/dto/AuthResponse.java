package com.reer.resumebuilder.resume_builder_api.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class AuthResponse {
//    @JsonProperty("_id")
    private String id;
    private String name;
    private String email;
    private String token;
    private String profileImageUrl;
    private String subscriptionPlan;
    private boolean emailVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
