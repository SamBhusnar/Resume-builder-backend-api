package com.reer.resumebuilder.resume_builder_api.security;


import com.reer.resumebuilder.resume_builder_api.documents.User;
import com.reer.resumebuilder.resume_builder_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository profileRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User profileEntity = profileRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User.builder()
                .username(profileEntity.getEmail())
                .password(profileEntity.getPassword())
                .authorities(Collections.emptyList()).build();

    }

}