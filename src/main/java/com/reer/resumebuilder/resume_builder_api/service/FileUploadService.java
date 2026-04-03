package com.reer.resumebuilder.resume_builder_api.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.reer.resumebuilder.resume_builder_api.documents.Resume;
import com.reer.resumebuilder.resume_builder_api.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.core.support.RepositoryMethodInvocationListener;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService {
    private final Cloudinary cloudinary;
    private final ResumeRepository resumeRepository;
    private final AuthService authService;
    private final RepositoryMethodInvocationListener repositoryMethodInvocationListener;

    // U
    public Map<String, String> uploadSingleImage(MultipartFile file) throws IOException {
        log.info("Uploading image: {}", file);
        Map<String, Object> upload = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "resource_type", "image"
        ));
        log.info("Image uploaded successfully: {}", upload.get("secure_url").toString());
        String secureUrl = upload.get("secure_url").toString();
        log.info("Image uploaded successfully: {}", secureUrl);
        return Map.of("imageUrl", secureUrl);
    }

    public Map<String, String> uploadResumeImages(String resumeId, String userId, MultipartFile thumbnail, MultipartFile profileImage) throws IOException {
        Resume resume = resumeRepository.findByIdAndUserId(resumeId, userId).orElseThrow(() -> {
            return new RuntimeException("Resume not found");
        });
        // save the thumbnail and profile image to cloudinary and get the url
        // create hashmap and store the thumbnail and profile image urls

        Map<String, String> imageMap = new HashMap<>();

        if (Objects.nonNull(thumbnail)) {
            Map<String, String> thumbnailData = uploadSingleImage(thumbnail);
            imageMap.put("thumbnail", thumbnailData.get("imageUrl"));
            resume.setThumbnailLink(thumbnailData.get("imageUrl").toString());

        }
        if (Objects.nonNull(profileImage)) {
            Map<String, String> profileImageData = uploadSingleImage(profileImage);
            imageMap.put("profileImage", profileImageData.get("imageUrl"));
            // update the resume with the new image urls
            if (Objects.isNull(resume.getProfileInfo())) {
                resume.setProfileInfo(new Resume.ProfileInfo());
            }

            resume.getProfileInfo().setProfilePreviewUrl(profileImageData.get("imageUrl").toString());

        }

        resumeRepository.save(resume);
        imageMap.put("message", "Image uploaded successfully");
        return imageMap;
    }
}
