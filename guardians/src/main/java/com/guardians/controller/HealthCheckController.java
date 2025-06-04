package com.guardians.controller;

import com.guardians.dto.admin.HealthCheckResponse;
import com.guardians.service.admin.HealthCheckService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/admin/health") // API 기본 경로
public class HealthCheckController {

    private final HealthCheckService healthCheckService;

    public HealthCheckController(HealthCheckService healthCheckService) {
        this.healthCheckService = healthCheckService;
    }

    @GetMapping("/argocd")
    public ResponseEntity<HealthCheckResponse> getArgoCDHealth() {
        return ResponseEntity.ok(healthCheckService.checkArgoCDHealth());
    }

    @GetMapping("/harbor")
    public ResponseEntity<HealthCheckResponse> getHarborHealth() {
        return ResponseEntity.ok(healthCheckService.checkHarborHealth());
    }

    @GetMapping("/grafana")
    public ResponseEntity<HealthCheckResponse> getGrafanaHealth() {
        return ResponseEntity.ok(healthCheckService.checkGrafanaHealth());
    }

    @GetMapping("/jenkins")
    public ResponseEntity<HealthCheckResponse> getJenkinsHealth() {
        return ResponseEntity.ok(healthCheckService.checkJenkinsHealth());
    }

    @GetMapping("/all")
    public ResponseEntity<List<HealthCheckResponse>> getAllServicesHealth() {
        List<HealthCheckResponse> healthStatuses = Arrays.asList(
                healthCheckService.checkArgoCDHealth(),
                healthCheckService.checkHarborHealth(),
                healthCheckService.checkGrafanaHealth(),
                healthCheckService.checkJenkinsHealth()
        );

        return ResponseEntity.ok(healthStatuses);
    }
}