package com.csye6225.cloud.webapp.controller;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
public class HealthCheckController {

    @Autowired
    private SessionFactory sessionFactory;

    @RequestMapping("/healthz?")
    public ResponseEntity<Void> handleParamRequests() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .header("Cache-Control", "no-cache")
                .build();
    }

    @GetMapping("/healthz")
    public ResponseEntity<Void> HealthCheck(@RequestBody(required = false) String body, @RequestParam(required = false) Map<String, String> allParams){
        if ((body != null && !body.isEmpty())|| !allParams.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("Cache-Control", "no-cache")
                    .build();
        }


        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executor.submit(() -> {
            try (Session session = sessionFactory.openSession()) {
                session.createNativeQuery("SELECT 1").getSingleResult();
                return true;
            } catch (Exception e) {
                return false;
            }
        });

        try {
            boolean result = future.get(3, TimeUnit.SECONDS);
            if (result) {
                return ResponseEntity.ok()
                        .header("Cache-Control", "no-cache")
                        .build();
            } else {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .header("Cache-Control", "no-cache")
                        .build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .header("Cache-Control", "no-cache")
                    .build();
        } finally {
            executor.shutdownNow();
        }
    }

    @RequestMapping(value = "/healthz", method = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH,  RequestMethod.HEAD, RequestMethod.OPTIONS})
    public ResponseEntity<Void> methodNotAllowed() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .header("Cache-Control", "no-cache")
                .build();
    }

    @RequestMapping("/**")
    public ResponseEntity<Void> handleAllOtherRequests() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .header("Cache-Control", "no-cache")
                .build();
    }

    


}
