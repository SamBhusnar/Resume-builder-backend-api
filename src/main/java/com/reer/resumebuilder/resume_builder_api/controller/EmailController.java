package com.reer.resumebuilder.resume_builder_api.controller;

import com.reer.resumebuilder.resume_builder_api.service.EmailService;
import com.reer.resumebuilder.resume_builder_api.util.AppConstant;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping(AppConstant.API_VERSION + AppConstant.EMAIL)
@Slf4j
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping(value = AppConstant.EMAIL_RESUME, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> sendResumeByEmail(
            @RequestPart("recipientEmail") String recipientEmail,
            @RequestPart("subject") String subject,
            @RequestPart("message") String body,
            @RequestPart("pdfFile") MultipartFile pdfFile
    ) throws IOException, MessagingException {

        Map<String, Object> response = new HashMap<>();
        if (Objects.isNull(recipientEmail) || Objects.isNull(pdfFile)) {
            response.put("success", false);
            response.put("message", "Recipient email and PDF file are required.");
            return ResponseEntity.badRequest().body(response);
        }
        byte[] pdfBytes = pdfFile.getBytes();
        String originalFilename = pdfFile.getOriginalFilename();

        String filename = Objects.nonNull(originalFilename) ? originalFilename : "resume.pdf";
        String emailSubject = Objects.nonNull(subject) ? subject : "Resume Application";
        String emailBody = Objects.nonNull(body) ? body : "Please find the attached resume.\n\n Best Regards";
        emailService.sendEmailWithAttachment(recipientEmail, emailSubject, emailBody, pdfBytes, filename);
        response.put("success", true);
        response.put("message", "Email sent successfully.");
        return ResponseEntity.ok(response);
        
    }
}
