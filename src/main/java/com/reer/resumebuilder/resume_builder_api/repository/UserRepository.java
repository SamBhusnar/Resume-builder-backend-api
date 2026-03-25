package com.reer.resumebuilder.resume_builder_api.repository;

import com.reer.resumebuilder.resume_builder_api.documents.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);


}
