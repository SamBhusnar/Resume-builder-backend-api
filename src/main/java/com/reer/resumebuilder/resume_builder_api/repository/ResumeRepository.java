package com.reer.resumebuilder.resume_builder_api.repository;

import com.reer.resumebuilder.resume_builder_api.documents.Resume;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository extends MongoRepository<Resume, String> {

    List<Resume> findByUserIdOrderByUpdatedAtDesc(String id);

    // find by resUme with User id of cUrrent User in secUrity context
    Optional<Resume> findByIdAndUserId(String id, String userId);

}

