package com.example.financialapp.controller;

import com.example.financialapp.config.ExternalServiceProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class AccountController {

    private final ExternalServiceProperties externalServiceProperties;
    private final DataSource dataSource;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    public AccountController(ExternalServiceProperties externalServiceProperties, DataSource dataSource) {
        this.externalServiceProperties = externalServiceProperties;
        this.dataSource = dataSource;
    }

    @GetMapping("/")
    public String home() {
        return "Financial application is running successfully.";
    }

    @GetMapping("/health-check")
    public Map<String, String> healthCheck() {
        Map<String, String> response = new LinkedHashMap<>();
        response.put("status", "UP");
        response.put("application", applicationName);
        response.put("activeProfile", activeProfile);
        return response;
    }

    @GetMapping("/config")
    public Map<String, String> getConfig() throws Exception {
        Map<String, String> response = new LinkedHashMap<>();
        response.put("application", applicationName);
        response.put("activeProfile", activeProfile);
        response.put("paymentServiceUrl", externalServiceProperties.getPaymentUrl());
        response.put("auditServiceUrl", externalServiceProperties.getAuditUrl());

        try (Connection connection = dataSource.getConnection()) {
            response.put("databaseUrl", connection.getMetaData().getURL());
        }

        return response;
    }
}
