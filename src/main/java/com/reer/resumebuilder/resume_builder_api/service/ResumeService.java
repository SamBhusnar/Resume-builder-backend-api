package com.reer.resumebuilder.resume_builder_api.service;

import com.reer.resumebuilder.resume_builder_api.documents.Resume;
import com.reer.resumebuilder.resume_builder_api.documents.User;
import com.reer.resumebuilder.resume_builder_api.dto.AuthResponse;
import com.reer.resumebuilder.resume_builder_api.dto.CreateResumeRequest;
import com.reer.resumebuilder.resume_builder_api.repository.ResumeRepository;
import com.reer.resumebuilder.resume_builder_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
//    private final AuthService authService;

    private final AuthService authService;


    public Resume createResume(CreateResumeRequest request) {

        Resume resume = new Resume();
        // get current user id from security context
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // get user object by email that is name
        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            return new RuntimeException("User not found");
        });
        // convert user object to user service from userServie service
        AuthResponse response = authService.toResponse(user);
        resume.setUserId(response.getId());
        resume.setTitle(request.getTitle());
        setDefaultResumeData(resume);
        // save resume
        return resumeRepository.save(resume);

    }

    private void setDefaultResumeData(Resume resume) {
        resume.setProfileInfo(new Resume.ProfileInfo());
        resume.setContactInfo(new Resume.ContactInfo());
        resume.setEducation(new ArrayList<>());
        resume.setWorkExperiences(new ArrayList<>());
        resume.setSkills(new ArrayList<>());
        resume.setProjects(new ArrayList<>());
        resume.setCertifications(new ArrayList<>());
        resume.setLanguages(new ArrayList<>());
        resume.setInterests(new ArrayList<>());
        resume.setTemplate(new Resume.Template());
        resume.setThumbnailLink(null);
    }


    public List<Resume> getUserResumes(String userId) {
        log.info("inside getUserResumes userId: {}", userId);
        return resumeRepository.findByUserIdOrderByUpdatedAtDesc(userId);

    }

    public Resume getResumeById(String id) {
        log.info("inside getResumeById id: {}", id);
        String userId = authService.getCurrentUser().getId();
        return resumeRepository.findByIdAndUserId(id, userId).orElseThrow(() -> {
            return new RuntimeException("Resume not found");
        });
    }

    public Resume updateResume(String id, Resume resume, String userId) {
        log.info("inside updateResume id: {}", id);
        Resume existingResume = resumeRepository.findByIdAndUserId(id, userId).orElseThrow(() -> {
            return new RuntimeException("Resume not found");
        });
        existingResume.setTitle(resume.getTitle());
        existingResume.setThumbnailLink(resume.getThumbnailLink());
        existingResume.setTemplate(resume.getTemplate());
        existingResume.setProfileInfo(resume.getProfileInfo());
        existingResume.setContactInfo(resume.getContactInfo());
        existingResume.setEducation(resume.getEducation());
        existingResume.setWorkExperiences(resume.getWorkExperiences());
        existingResume.setSkills(resume.getSkills());
        existingResume.setProjects(resume.getProjects());
        existingResume.setCertifications(resume.getCertifications());
        existingResume.setLanguages(resume.getLanguages());
        existingResume.setInterests(resume.getInterests());

        return resumeRepository.save(existingResume);
    }

    public void deleteResume(String resumeId, String userId) {
        log.info("inside deleteResume id: {}", resumeId);
        Resume resume = resumeRepository.findByIdAndUserId(resumeId, userId).orElseThrow(() -> {
            return new RuntimeException("Resume not found");
        });
        resumeRepository.delete(resume);
        log.info("Resume deleted successfully");
    }
}
