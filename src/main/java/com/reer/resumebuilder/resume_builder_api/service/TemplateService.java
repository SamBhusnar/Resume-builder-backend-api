package com.reer.resumebuilder.resume_builder_api.service;

import com.reer.resumebuilder.resume_builder_api.documents.User;
import com.reer.resumebuilder.resume_builder_api.repository.UserRepository;
import com.reer.resumebuilder.resume_builder_api.util.AppConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateService {
    private final AuthService authService;
    private final UserRepository userRepository;

    public Map<String, Object> getTemplate(String userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> {
            return new RuntimeException("User not found");
        });
        List<String> availableTemplates;

        boolean isPremium = AppConstant.PREMIUM.equalsIgnoreCase(user.getSubscriptionPlan());

        if (isPremium) {
            availableTemplates = List.of("01", "02", "03");
        } else {
            availableTemplates = List.of("01");
        }

        Map<String, Object> restrictions = new HashMap<>();
        restrictions.put("availableTemplates", availableTemplates);
        restrictions.put("allTemplates", List.of("01", "02", "03"));
        restrictions.put("subscriptionPlan", user.getSubscriptionPlan());
        restrictions.put("isPremium", isPremium);
        return restrictions;
    }
}
