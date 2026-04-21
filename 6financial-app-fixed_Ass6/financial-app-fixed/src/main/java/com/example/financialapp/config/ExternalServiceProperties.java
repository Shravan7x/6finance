package com.example.financialapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "external.service")
public class ExternalServiceProperties {

    private String paymentUrl;
    private String auditUrl;

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

    public String getAuditUrl() {
        return auditUrl;
    }

    public void setAuditUrl(String auditUrl) {
        this.auditUrl = auditUrl;
    }
}
