package com.guardians.service.admin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guardians.dto.admin.HealthCheckResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Service
public class HealthCheckService {

    private static final Logger logger = LoggerFactory.getLogger(HealthCheckService.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.service.argocd.health.url}")
    private String argocdHealthUrl;

    @Value("${app.service.harbor.health.url}")
    private String harborHealthUrl;

    @Value("${app.service.grafana.health.url}")
    private String grafanaHealthUrl;

    @Value("${app.service.jenkins.health.url}")
    private String jenkinsHealthUrl;

    public HealthCheckService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public HealthCheckResponse checkArgoCDHealth() {
        String serviceName = "ArgoCD";
        logger.info("Checking {} health at URL: {}", serviceName, argocdHealthUrl);
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(argocdHealthUrl, String.class);
            String responseBody = response.getBody();

            if (response.getStatusCode() == HttpStatus.OK && responseBody != null && "OK".equalsIgnoreCase(responseBody.trim())) {
                return new HealthCheckResponse(serviceName, "HEALTHY", "Service is operational.", responseBody); // rawDetails 추가
            } else {
                logger.warn("{} status check returned: Status Code = {}, Body = '{}'",
                        serviceName, response.getStatusCode(), responseBody != null ? responseBody.trim() : "null");
                return new HealthCheckResponse(serviceName, "UNHEALTHY",
                        "Received status " + response.getStatusCode() + " but body was not 'OK'.",
                        responseBody); // rawDetails 추가
            }
        } catch (HttpClientErrorException e) {
            logger.error("{} HTTP error: {} - Response: {}", serviceName, e.getStatusCode(), e.getResponseBodyAsString(), e);
            return new HealthCheckResponse(serviceName, "ERROR_FETCHING",
                    "HTTP error: " + e.getStatusCode(),
                    e.getResponseBodyAsString());
        } catch (ResourceAccessException e) {
            logger.error("{} Network error: {}", serviceName, e.getMessage(), e);
            return new HealthCheckResponse(serviceName, "ERROR_FETCHING", "Network error, could not connect: " + e.getMessage());
        } catch (Exception e) {
            logger.error("{} Unexpected error: {}", serviceName, e.getMessage(), e);
            return new HealthCheckResponse(serviceName, "ERROR_FETCHING", "An unexpected error occurred: " + e.getMessage());
        }
    }

    public HealthCheckResponse checkHarborHealth() {
        String serviceName = "Harbor";
        logger.info("Checking {} health at URL: {}", serviceName, harborHealthUrl);
        try {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(harborHealthUrl, String.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                String responseBody = responseEntity.getBody();
                JsonNode root = objectMapper.readTree(responseBody);
                String overallStatus = root.path("status").asText();

                if ("healthy".equalsIgnoreCase(overallStatus)) {
                    return new HealthCheckResponse(serviceName, "HEALTHY", "Overall status: healthy.", responseBody);
                } else {
                    StringBuilder componentDetails = new StringBuilder();
                    if (root.has("components")) {
                        for (JsonNode component : root.path("components")) {
                            if (!"healthy".equalsIgnoreCase(component.path("status").asText())) {
                                componentDetails.append(component.path("name").asText())
                                        .append(": ")
                                        .append(component.path("status").asText())
                                        .append("; ");
                            }
                        }
                    }
                    String message = "Overall status: " + overallStatus +
                            (componentDetails.length() > 0 ? " Unhealthy components: " + componentDetails.toString() : "");
                    logger.warn("{} unhealthy. {}", serviceName, message);
                    return new HealthCheckResponse(serviceName, "UNHEALTHY", message, responseBody);
                }
            } else {
                logger.warn("{} unhealthy. Status: {}", serviceName, responseEntity.getStatusCode());
                return new HealthCheckResponse(serviceName, "UNHEALTHY",
                        "Received status " + responseEntity.getStatusCode(),
                        responseEntity.getBody());
            }
        } catch (HttpClientErrorException e) {
            logger.error("{} HTTP error: {} - {}", serviceName, e.getStatusCode(), e.getResponseBodyAsString(), e);
            return new HealthCheckResponse(serviceName, "ERROR_FETCHING", "HTTP error: " + e.getStatusCode(), e.getResponseBodyAsString());
        } catch (ResourceAccessException e) {
            logger.error("{} Network error: {}", serviceName, e.getMessage(), e);
            return new HealthCheckResponse(serviceName, "ERROR_FETCHING", "Network error, could not connect.");
        } catch (Exception e) {
            logger.error("{} Unexpected error: {}", serviceName, e.getMessage(), e);
            return new HealthCheckResponse(serviceName, "ERROR_FETCHING", "An unexpected error or parsing issue occurred.");
        }
    }

    public HealthCheckResponse checkGrafanaHealth() {
        String serviceName = "Grafana";
        logger.info("Checking {} health at URL: {}", serviceName, grafanaHealthUrl);
        try {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(grafanaHealthUrl, String.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                String responseBody = responseEntity.getBody();
                JsonNode root = objectMapper.readTree(responseBody);
                String dbStatus = root.path("database").asText();

                if ("ok".equalsIgnoreCase(dbStatus)) {
                    return new HealthCheckResponse(serviceName, "HEALTHY", "Database: OK; Version: " + root.path("version").asText(), responseBody);
                } else {
                    logger.warn("{} unhealthy. Database status: {}", serviceName, dbStatus);
                    return new HealthCheckResponse(serviceName, "UNHEALTHY", "Database status: " + dbStatus, responseBody);
                }
            } else {
                logger.warn("{} unhealthy. Status: {}", serviceName, responseEntity.getStatusCode());
                return new HealthCheckResponse(serviceName, "UNHEALTHY",
                        "Received status " + responseEntity.getStatusCode(),
                        responseEntity.getBody());
            }
        } catch (HttpClientErrorException e) {
            logger.error("{} HTTP error: {} - {}", serviceName, e.getStatusCode(), e.getResponseBodyAsString(), e);
            return new HealthCheckResponse(serviceName, "ERROR_FETCHING", "HTTP error: " + e.getStatusCode(), e.getResponseBodyAsString());
        } catch (ResourceAccessException e) {
            logger.error("{} Network error: {}", serviceName, e.getMessage(), e);
            return new HealthCheckResponse(serviceName, "ERROR_FETCHING", "Network error, could not connect.");
        } catch (Exception e) {
            logger.error("{} Unexpected error: {}", serviceName, e.getMessage(), e);
            return new HealthCheckResponse(serviceName, "ERROR_FETCHING", "An unexpected error or parsing issue occurred.");
        }
    }

    public HealthCheckResponse checkJenkinsHealth() {
        String serviceName = "Jenkins";
        logger.info("Checking {} basic availability at URL: {}", serviceName, jenkinsHealthUrl);
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(jenkinsHealthUrl, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return new HealthCheckResponse(serviceName, "HEALTHY", "Web interface is accessible.");
            }
            else if (response.getStatusCode() == HttpStatus.FORBIDDEN ||
                    (response.getBody() != null && response.getBody().toLowerCase().contains("authentication required"))) {
                logger.warn("{} requires authentication at {}. Web server is up.", serviceName, jenkinsHealthUrl);
                return new HealthCheckResponse(serviceName, "DEGRADED", "Authentication required, but web server is responsive.", response.getBody());
            }
            else {
                logger.warn("{} returned non-OK status: {}, Body: {}", serviceName, response.getStatusCode(), response.getBody());
                return new HealthCheckResponse(serviceName, "UNHEALTHY",
                        "Received status " + response.getStatusCode(),
                        response.getBody());
            }
        } catch (HttpClientErrorException e) { // 4xx, 5xx 에러 (403 포함)
            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                logger.warn("{} requires authentication at {}. Web server is up. HTTP Error: {}", serviceName, jenkinsHealthUrl, e.getStatusCode());
                return new HealthCheckResponse(serviceName, "DEGRADED",
                        "Authentication required, web server responsive. (" + e.getStatusCode() + ")",
                        e.getResponseBodyAsString());
            }
            logger.error("{} HTTP error: {} - {}", serviceName, e.getStatusCode(), e.getResponseBodyAsString(), e);
            return new HealthCheckResponse(serviceName, "ERROR_FETCHING",
                    "HTTP error: " + e.getStatusCode(),
                    e.getResponseBodyAsString());
        } catch (ResourceAccessException e) { // 네트워크 연결 불가 등
            logger.error("{} Network error: {}", serviceName, e.getMessage(), e);
            return new HealthCheckResponse(serviceName, "ERROR_FETCHING", "Network error, could not connect.");
        } catch (Exception e) {
            logger.error("{} Unexpected error: {}", serviceName, e.getMessage(), e);
            return new HealthCheckResponse(serviceName, "ERROR_FETCHING", "An unexpected error occurred.");
        }
    }
}