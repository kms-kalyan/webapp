package com.csye6225.cloud.webapp.controller;


import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.csye6225.cloud.webapp.service.HealthService;

import java.util.Map;

@RestController
@RequestMapping("/healthz")
public class HealthController{
    private final HealthService healthCheckService;

    public HealthController(HealthService healthCheckService) {
        this.healthCheckService = healthCheckService;
    }

    @GetMapping
    public ResponseEntity<Void> getHealthCheck(@RequestParam Map<String, String> queryParameter, @RequestBody(required = false) String payload, @RequestHeader(value = "authorization", required = false) String authorization) {
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().mustRevalidate());
        headers.setPragma("no-cache");
        headers.add("X-Content-Type-Options", "nosniff");
        if (null != authorization) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(headers).build();
        } else if (null != payload && !payload.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(headers).build();
        } else if (null != queryParameter && !queryParameter.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(headers).build();
        } else if (healthCheckService.isDatabaseConnected()) {
            return ResponseEntity.status(HttpStatus.OK).headers(headers).build();
        } else {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).headers(headers).build();
        }
    }

    @RequestMapping(method = {RequestMethod.PUT, RequestMethod.HEAD, RequestMethod.OPTIONS, RequestMethod.DELETE})
    public ResponseEntity<Void> handleHeadOptionsCall() {
        
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                         .header("Cache-Control", "no-cache")
                         .build();
    }
}
