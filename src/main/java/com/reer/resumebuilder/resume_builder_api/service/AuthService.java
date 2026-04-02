package com.reer.resumebuilder.resume_builder_api.service;


import com.reer.resumebuilder.resume_builder_api.documents.User;
import com.reer.resumebuilder.resume_builder_api.dto.AuthResponse;
import com.reer.resumebuilder.resume_builder_api.dto.LoginRequest;
import com.reer.resumebuilder.resume_builder_api.dto.RegisterRequest;
import com.reer.resumebuilder.resume_builder_api.exception.ResourceExistsException;
import com.reer.resumebuilder.resume_builder_api.exception.UserNotVerifiedException;
import com.reer.resumebuilder.resume_builder_api.repository.UserRepository;
import com.reer.resumebuilder.resume_builder_api.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final EmailService emailService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    @Value("${app.base.url:http://localhost:8080}")
    private String appUrl;


    public AuthResponse register(RegisterRequest registerRequest) {
        log.info("Inside register method , Registering new user: {}", registerRequest);

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            log.warn("Email {} is already in use", registerRequest.getEmail());
            throw new ResourceExistsException("user   already  exists with this email : " + registerRequest.getEmail());
        }

// save the user sing builder pattern

        User newUser = toDocument(registerRequest);

// save the user
        User savedUser = userRepository.save(newUser);
        // todo : trigger email for verification
        sendVerificationEmail(savedUser);
//give  authResponse
        return toResponse(savedUser);


    }

    private void sendVerificationEmail(User savedUser) {
        log.info("Sending verification email to {}", savedUser.getEmail());
        try {
            String link = appUrl + "/api/auth/verify-email?token=" + savedUser.getVerificationToken();
            String html = "<div style='font-family:sans-serif'>" +
                    "<h2>Verify your email  </h2>" +
                    "<p> Hi" + savedUser.getName() + ", please confirm your email to activate your account.</p>" +
                    "<p><a href='" + link
                    + "'style='display:inline-block;padding:10px 16px ; background-color: #6366f1;color: #fff;border-radius:6px; text-decoration: none'>Verify Email</a></p>"
                    +
                    "<p>0r copy this link: " + link + "</p>"
                    + "<p>This link expires in 24 hours. </p>" +

                    "</div>";
            emailService.sendHtmlEmail(savedUser.getEmail(), "Verify your email", html);
            log.info("Verification email sent to {}", savedUser.getEmail());
        } catch (Exception e) {
            log.error("Failed to send verification email to {}", savedUser.getEmail());
            throw new RuntimeException("Failed to send verification email : " + e.getMessage());
        }
    }

    public void verifyEmail(String token) {
        log.info("Verifying email with token: {}", token);
        User verifiedUser = userRepository.findByVerificationToken(token)

                .map(user -> {
                    if (user.getVerificationToken() != null && user.getVerificationExpires().isBefore(LocalDateTime.now())) {
                        log.warn("Verification token {} has expired", token);
                        throw new RuntimeException("Verification token has expired");
                    }
                    user.setEmailVerified(true);
                    user.setVerificationToken(null);
                    user.setVerificationExpires(null);
                    return userRepository.save(user);
                })
                .orElseThrow(() -> {
                    log.warn("Invalid verification token: {}", token);
                    return new RuntimeException("Invalid verification token");
                });
        log.info("Email verified successfully");

    }
    // create method that convert user to authResponse


    private AuthResponse toResponse(User user) {
        log.info("Converting user to authResponse");
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
        log.info("Converting registerRequest to User");
        return User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .profileImageUrl(registerRequest.getProfileImageUrl())
                .subscriptionPlan("Basic")
                .emailVerified(false)
                .verificationToken(UUID.randomUUID().toString())
                .verificationExpires(LocalDateTime.now().plusHours(24))
                .build();
    }

    public AuthResponse login(LoginRequest loginRequest) {
        log.info("Inside login method");
        User existingUser = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> {
                    log.warn("Invalid login attempt for email: {}", loginRequest.getEmail());
                    return new UsernameNotFoundException("Invalid email or password");
                });
        if (passwordEncoder.matches(loginRequest.getPassword(), existingUser.getPassword())) {
            log.info("Login successful for email: {}", loginRequest.getEmail());
            if (!existingUser.isEmailVerified()) {
                throw new UserNotVerifiedException("User not verified");
            }
            String jwtToken = jwtUtil.generateToken(existingUser.getEmail());
            AuthResponse response = toResponse(existingUser);
            response.setToken(jwtToken);
            return response;

        } else {
            log.warn("Invalid login attempt for email: {}", loginRequest.getEmail());
            throw new RuntimeException("Invalid email or password");
        }


    }

    public void resendVerificationEmail(String email) {
        log.info("Resending verification email to {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });
        if (user.isEmailVerified()) {
            log.warn("User with email {} is already verified", email);
            throw new RuntimeException("User is already verified");
        }
        user.setVerificationToken(UUID.randomUUID().toString());
        user.setVerificationExpires(LocalDateTime.now().plusHours(24));
        userRepository.save(user);
        sendVerificationEmail(user);
    }

    public AuthResponse getCurrentUser() {
        log.info("Getting current user inside service");
        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null) {
            log.warn("Security context is null");
            throw new RuntimeException("Security context is null");
        }

        Authentication authentication = context.getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            log.warn("You've deleted your account already but you are using old jwt token :{}", email);
            return new UsernameNotFoundException("You've deleted your account already but you are using old jwt token" + email);
        });

        return toResponse(user);
    }
}
