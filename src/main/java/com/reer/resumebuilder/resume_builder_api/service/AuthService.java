package com.reer.resumebuilder.resume_builder_api.service;


import com.reer.resumebuilder.resume_builder_api.documents.User;
import com.reer.resumebuilder.resume_builder_api.dto.AuthResponse;
import com.reer.resumebuilder.resume_builder_api.dto.RegisterRequest;
import com.reer.resumebuilder.resume_builder_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;


    public AuthResponse register(RegisterRequest registerRequest) {
        log.info("Inside register method , Registering new user: {}", registerRequest);

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            log.warn("Email {} is already in use", registerRequest.getEmail());
            throw new RuntimeException("user   already  exists with this email : " + registerRequest.getEmail());
        }

// save the user sing builder pattern

        User newUser = toDocument(registerRequest);

// save the user
        User savedUser = userRepository.save(newUser);
        // todo : trigger email for verification
//give  authResponse
        return toResponse(savedUser);


    }
    // create method that convert user to authResponse

    private AuthResponse toResponse(User user) {
        return AuthResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .profileImageUrl(user.getProfileImageUrl())
                .subscriptionPlan(user.getSubscriptionPlan())
                .emailVerified(user.isEmailVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();

    }

    // for request
    private User toDocument(RegisterRequest registerRequest) {
        return User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(registerRequest.getPassword())
                .profileImageUrl(registerRequest.getProfileImageUrl())
                .subscriptionPlan("Basic")
                .emailVerified(false)
                .verificationToken(UUID.randomUUID().toString())
                .verificationExpires(LocalDateTime.now().plusHours(24))
                .build();
    }
}


