package com.Projects.ResumeBuilder.Controller;


import com.Projects.ResumeBuilder.Service.TemplatesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.Projects.ResumeBuilder.Utilities.AppConstants.TEMPLATES;

@RequiredArgsConstructor
@RestController
@RequestMapping(TEMPLATES)
@Slf4j
public class TemplatesController {

    private final TemplatesService templatesService;
    @GetMapping
    public ResponseEntity<?> getTemplate(Authentication authentication){
        Map<String,Object> response = templatesService.getTemplates(authentication.getPrincipal());
        return ResponseEntity.ok(response);
    }

}
