package com.reer.resumebuilder.resume_builder_api.controller;

import com.reer.resumebuilder.resume_builder_api.dto.AuthResponse;
import com.reer.resumebuilder.resume_builder_api.dto.LoginRequest;
import com.reer.resumebuilder.resume_builder_api.dto.RegisterRequest;
import com.reer.resumebuilder.resume_builder_api.service.AuthService;
import com.reer.resumebuilder.resume_builder_api.service.fileUploadService;
import com.reer.resumebuilder.resume_builder_api.util.AppConstant;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(AppConstant.AUTH_CONTROLLER)
// authentication is not applied yet any  user can access any others users data and change it it is critical it should be changed
public class AuthController {
    private final AuthService authService;

    private final fileUploadService fileUploadService;

    // status
    @GetMapping("/status")
    public ResponseEntity<?> status() {
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    @PostMapping(AppConstant.REGISTER)
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {

        log.info("Registering new user: {}", registerRequest);
        AuthResponse register = authService.register(registerRequest);
        log.info("New user was registered  : {}", register);
        return ResponseEntity.status(HttpStatus.CREATED).body(register);
    }

    @GetMapping(AppConstant.VERIFY_EMAIL)
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        log.info("Verifying email with token: {}", token);
        authService.verifyEmail(token);
        log.info("Email verified successfully");
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Email verified successfully"));


    }

    @PostMapping(AppConstant.UPLOAD_IMAGE)
    public ResponseEntity<?> uploadImage(@RequestPart("image") MultipartFile file) throws IOException {
        log.info("Uploading image: {}", file);

        Map<String, String> imageUrl = fileUploadService.uploadSingleImage(file);
        log.info("Image uploaded successfully: {}", imageUrl);
        return ResponseEntity.ok(imageUrl);

    }

    @PostMapping(AppConstant.LOGIN)
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    // make endpoint for logout
    @GetMapping("/sam")
    public ResponseEntity<?> logout() {
        log.info("Logout");
        return ResponseEntity.ok("sam");
    }

    @PostMapping(AppConstant.RE_SEND_VERIFICATION_EMAIL)
    public ResponseEntity<?> resendVerificationEmail(@RequestBody Map<String, String> body) {
        log.info("Resending verification email with token: {}", body.get("email"));
        String email = body.get("email");
        if (Objects.isNull(email) || email.isEmpty()) {
            log.warn("Email is missing in the request body");
            throw new RuntimeException("Email is empty or email is null --> email should not be empty or null");
        }
        authService.resendVerificationEmail(email);

        log.info("Resend verification email");
        return ResponseEntity.ok(Map.of("success", true, "message", "Verification email sent successfully"));
    }

    // engpoint to get current user
    @GetMapping(AppConstant.CURRENT_USER)
    public ResponseEntity<?> getCurrentUser() {
        log.info("Getting current user inside controller");

        return ResponseEntity.ok(authService.getCurrentUser());
    }

}

