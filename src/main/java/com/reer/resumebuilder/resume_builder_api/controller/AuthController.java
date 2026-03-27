package com.reer.resumebuilder.resume_builder_api.controller;

import com.reer.resumebuilder.resume_builder_api.dto.AuthResponse;
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

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(AppConstant.AUTH_CONTROLLER)
public class AuthController {
    private final AuthService authService;

    private final fileUploadService fileUploadService;

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

}

