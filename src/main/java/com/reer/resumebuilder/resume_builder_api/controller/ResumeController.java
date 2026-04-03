package com.reer.resumebuilder.resume_builder_api.controller;

import com.reer.resumebuilder.resume_builder_api.documents.Resume;
import com.reer.resumebuilder.resume_builder_api.dto.AuthResponse;
import com.reer.resumebuilder.resume_builder_api.dto.CreateResumeRequest;
import com.reer.resumebuilder.resume_builder_api.service.AuthService;
import com.reer.resumebuilder.resume_builder_api.service.FileUploadService;
import com.reer.resumebuilder.resume_builder_api.service.ResumeService;
import com.reer.resumebuilder.resume_builder_api.util.AppConstant;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.reer.resumebuilder.resume_builder_api.util.AppConstant.API_RESUME;

@RestController
@RequestMapping(AppConstant.API_VERSION + API_RESUME)
@RequiredArgsConstructor
@Slf4j
public class ResumeController {
    private final ResumeService resumeService;
    private final AuthService authService;
    private final FileUploadService fileService;

    @PostMapping
    public ResponseEntity<?> createResume(@Valid @RequestBody CreateResumeRequest request) {
        Resume newResume = resumeService.createResume(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newResume);
    }


    @GetMapping
    public ResponseEntity<?> getUserResumes() {
        log.info("inside getUserResumes in controller");
        AuthResponse currentUser = authService.getCurrentUser();
        List<Resume> userResumes = resumeService.getUserResumes(currentUser.getId());
        return ResponseEntity.ok(userResumes);
    }

    // get resUme by id
    @GetMapping(AppConstant.ID)
    public ResponseEntity<?> getResumeById(@PathVariable String id) {
        log.info("inside getResumeById in controller");
        return ResponseEntity.ok(resumeService.getResumeById(id));

    }

    // update resume
    @PutMapping(AppConstant.ID)
    public ResponseEntity<?> updateResume(@PathVariable String id, @RequestBody Resume resume) {

        String userId = authService.getCurrentUser().getId();
        return ResponseEntity.ok(resumeService.updateResume(id, resume, userId));

    }

    // Upload resUme image
    @PutMapping(AppConstant.ID + AppConstant.UPLOAD_IMAGES)
    public ResponseEntity<?> uploadResumeImage(
            @PathVariable("id") String resumeId,
            @RequestPart(value = "thumbnail", required = true) MultipartFile thumbnail,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) throws IOException {
        String userId = authService.getCurrentUser().getId();
        Map<String, String> response = fileService.uploadResumeImages(resumeId, userId, thumbnail, profileImage);

        return ResponseEntity.ok(response);
    }

    // deleting resUme
    @DeleteMapping(AppConstant.ID)
    public ResponseEntity<?> deleteResume(@PathVariable("id") String resumeId) {
        log.info("inside deleteResume in controller");
        String userId = authService.getCurrentUser().getId();
        resumeService.deleteResume(resumeId, userId);
        log.info("Resume deleted successfully");
        return ResponseEntity.ok(Map.of("message", "Resume deleted successfully"));


    }
}

