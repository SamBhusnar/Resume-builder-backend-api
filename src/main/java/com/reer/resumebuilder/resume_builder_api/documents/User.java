package com.reer.resumebuilder.resume_builder_api.documents;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    private String id;
    private String name;
    private String email;
    private String password;
    private String profileImageUrl;
    // subscription plan
    private String subscriptionPlan = "basic";
    private boolean emailVerified = false;
    private String verificationToken;
    private LocalDateTime verificationExpires;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
