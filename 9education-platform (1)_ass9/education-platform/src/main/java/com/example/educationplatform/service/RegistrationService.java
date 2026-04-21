package com.example.educationplatform.service;

import com.example.educationplatform.dto.RegistrationRequest;
import com.example.educationplatform.exception.PasswordMismatchException;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class RegistrationService {

    public Map<String, String> registerStudent(RegistrationRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("Password and confirm password do not match");
        }

        Map<String, String> response = new LinkedHashMap<>();
        response.put("message", "Student registration successful");
        response.put("studentName", request.getFullName());
        response.put("email", request.getEmail());
        response.put("course", request.getCourse());
        response.put("age", String.valueOf(request.getAge()));
        return response;
    }
}
