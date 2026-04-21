package com.example.educationplatform.controller;

import com.example.educationplatform.dto.RegistrationRequest;
import com.example.educationplatform.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerStudent(@Valid @RequestBody RegistrationRequest request) {
        return ResponseEntity.ok(registrationService.registerStudent(request));
    }
}
