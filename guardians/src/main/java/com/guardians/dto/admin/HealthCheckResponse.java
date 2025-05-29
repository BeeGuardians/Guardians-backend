package com.guardians.dto.admin;

public class HealthCheckResponse {
    private String serviceName;
    private String status;
    private String message;
    private String rawDetails;

    // 생성자, Getter, Setter
    public HealthCheckResponse(String serviceName, String status, String message) {
        this.serviceName = serviceName;
        this.status = status;
        this.message = message;
    }

    public HealthCheckResponse(String serviceName, String status, String message, String rawDetails) {
        this.serviceName = serviceName;
        this.status = status;
        this.message = message;
        this.rawDetails = rawDetails;
    }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getRawDetails() { return rawDetails; }
    public void setRawDetails(String rawDetails) { this.rawDetails = rawDetails; }
}