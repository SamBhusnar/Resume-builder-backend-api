package com.reer.resumebuilder.resume_builder_api.controller;

import com.reer.resumebuilder.resume_builder_api.service.AuthService;
import com.reer.resumebuilder.resume_builder_api.service.TemplateService;
import com.reer.resumebuilder.resume_builder_api.util.AppConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(AppConstant.API_VERSION + AppConstant.TEMPLATE)
public class TemplateController {

    private final TemplateService templateService;
    private final AuthService authService;


    @GetMapping
    public ResponseEntity<?> getTemplate() {
        // step 1:call the service method
        String userId = authService.getCurrentUser().getId();

        Map<String, Object> template = templateService.getTemplate(userId);

        // step 2: return the response
        return ResponseEntity.ok(template);
    }

}
