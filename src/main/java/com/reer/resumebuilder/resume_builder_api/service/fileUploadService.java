package com.reer.resumebuilder.resume_builder_api.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class fileUploadService {
    private final Cloudinary cloudinary;

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
}
