package com.csye6225.cloud.webapp.controller;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.apache.coyote.BadRequestException;
import org.apache.tomcat.util.http.parser.Authorization;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.csye6225.cloud.webapp.configuration.SessionConfig;
import com.csye6225.cloud.webapp.model.User;
import com.csye6225.cloud.webapp.repository.UserDAO;
import com.csye6225.cloud.webapp.service.UserService;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
public class HealthCheckController{

    @Autowired
    SessionConfig sessionConfig;

    @Autowired
    UserDAO userDAO;

    @Autowired
    UserService userService;

    Logger logger;

    @GetMapping("/healthz")
    public ResponseEntity<Void> HealthCheck(@RequestBody(required = false) String body, @RequestParam(required = false) Map<String, String> allParams){
        
        if ((body != null && !body.isEmpty())|| !allParams.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("Cache-Control", "no-cache")
                    .build();
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        @SuppressWarnings("deprecation")
        Future<Boolean> future = executor.submit(() -> {
            try (Session session = sessionConfig.getSessionFactory().openSession()) {
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
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .header("Cache-Control", "no-cache")
                .build();
    }

    
    @PostMapping("/v1/user")
    public ResponseEntity<Map<String,String>> postMethodName(@RequestBody(required = true) Map<String,String> reqBody) {
        
        if(reqBody.isEmpty() || reqBody == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .header("Cache-Control", "no-cache")
                .build();
        }
        
        
        User user = new User();
        user.setEmail(reqBody.get("email"));
        user.setFirstName(reqBody.get("first_name"));
        user.setLastName(reqBody.get("last_name"));
        user.setPassword(reqBody.get("password"));

        
        
        if(userService.createUser(user) == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .header("Cache-Control", "no-cache")
            .build();
        }
        reqBody.remove("password");
        reqBody.put("id", String.valueOf(user.getId()));
        reqBody.put("account_created", user.getAccountCreated().toString());
        reqBody.put("account_updated", user.getAccountUpdated().toString());
        
        ResponseEntity<Map<String,String>> responseEntity = new ResponseEntity<>(reqBody,null,HttpStatus.CREATED);
        
        return responseEntity;
    }

    @PutMapping("/v1/user/self")
    public ResponseEntity<User> updateUserInfo(@RequestBody Map<String,String> reqBody) {
        User updatedUser = new User();
        String email = reqBody.get("email");
        
        updatedUser.setEmail(email);
        updatedUser.setFirstName(reqBody.get("first_name"));
        updatedUser.setLastName(reqBody.get("last_name"));
        updatedUser.setPassword(reqBody.get("password"));
        
        try {
            User user = userService.updateUser(updatedUser, email);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } 
    }

    @GetMapping("/v1/user/self")
    public ResponseEntity<?> checkUser(@RequestHeader("Authorization") String authorizationHeader) {
        
        System.out.println("Inside get, "+authorizationHeader);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Basic ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing or invalid Authorization header");
        }

        // Decode Base64 encoded credentials
        String base64Credentials = authorizationHeader.substring("Basic ".length()).trim();
        String credentials = new String(Base64Utils.decodeFromString(base64Credentials));
        
        // Split username and password
        final String[] values = credentials.split(":", 2);
        if (values.length != 2) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid credentials format");
        }
        
        String email = values[0];
        String password = values[1];

        User user = userService.getUserByEmail(email);

        if (user == null || !userService.checkPassword(user, password)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found or invalid password");
        }

        // Exclude password from the response
        user.setPassword(null);

        return ResponseEntity.ok(null);
    }


}
