package com.csye6225.cloud.webapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import com.csye6225.cloud.webapp.dto.UserRequest;
import com.csye6225.cloud.webapp.dto.UserResponse;
import com.csye6225.cloud.webapp.exception.UserNotCreatedException;
import com.csye6225.cloud.webapp.exception.UserNotFoundException;
import com.csye6225.cloud.webapp.exception.UserNotUpdatedException;
import com.csye6225.cloud.webapp.exception.UserNotVerifiedException;
import com.csye6225.cloud.webapp.service.UserService;
import com.csye6225.cloud.webapp.service.MetricsService;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/v1/user")
public class UserController {

    Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final MetricsService metricsService;
    private final HttpHeaders headers;

    
    public UserController(UserService userService, MetricsService metricsService) {
        this.userService = userService;
        this.metricsService = metricsService;
        this.headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().mustRevalidate());
        headers.setPragma("no-cache");
        headers.add("X-Content-Type-Options", "nosniff");
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest user) throws UserNotCreatedException {
        long startTime = System.currentTimeMillis();

        logger.info("In POST user method for user: " + user.getEmail());
        try {
            UserResponse createdUserDto = userService.createdUser(user);
            logger.info("POST Request Success and newly created user: " + createdUserDto);

            long duration = System.currentTimeMillis() - startTime;

            metricsService.recordApiCall("/v1/user");
            metricsService.recordApiDuration("/v1/user", duration);

            return ResponseEntity.status(HttpStatus.CREATED).headers(this.headers).body(createdUserDto);
        } catch (UserNotCreatedException uex) {
            logger.error("Exception occurred for user while creating: " + user.getEmail());
            throw uex;
        } catch (Exception e) {
            logger.error("Exception occurred for user while creating: " + user.getEmail());
            throw new UserNotCreatedException("User not created");
        }
    }

    @GetMapping("/self")
    public ResponseEntity<UserResponse> getUser(@RequestHeader(name = "Authorization", required = true) String authorization) throws UserNotFoundException, UserNotVerifiedException {
        long startTime = System.currentTimeMillis();

        logger.info("Inside get self: " + authorization);
        UserResponse userDto = userService.getUser(authorization);
        logger.info("GET Request Success and user: " + userDto);

        long duration = System.currentTimeMillis() - startTime;

        metricsService.recordApiCall("/v1/user/self");
        metricsService.recordApiDuration("/v1/user/self", duration);

        return ResponseEntity.status(HttpStatus.OK).headers(this.headers).body(userDto);
    }

    @PutMapping(path = "/self", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> updateUser(@RequestParam Map<String, String> queryParameter, 
                                           @RequestBody UserRequest user, 
                                           @RequestHeader("authorization") String authorization) throws UserNotUpdatedException, UserNotFoundException, UserNotVerifiedException {
        long startTime = System.currentTimeMillis();

        logger.info("The request given by the user: " + user);
        if (null != queryParameter && !queryParameter.isEmpty()) {
            logger.error("Query parameter is given");

            long duration = System.currentTimeMillis() - startTime;

            metricsService.recordApiCall("/v1/user/self");
            metricsService.recordApiDuration("/v1/user/self", duration);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(this.headers).build();
        }
        String message = "";
        try {
            message = userService.updateUser(user, authorization);
            logger.info("PUT request is success for user:" + user.getFirstName());

            long duration = System.currentTimeMillis() - startTime;

            metricsService.recordApiCall("/v1/user/self");
            metricsService.recordApiDuration("/v1/user/self", duration);

            return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(this.headers).build();
        } catch (UserNotUpdatedException | UserNotFoundException | UsernameNotFoundException uex) {
            logger.error("Exception occurred for user while updating: " + user.getFirstName());

            long duration = System.currentTimeMillis() - startTime;

            metricsService.recordApiCall("/v1/user/self");
            metricsService.recordApiDuration("/v1/user/self", duration);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).headers(this.headers).body(message);
        } catch (Exception e) {
            logger.error("Exception occurred for user while updating: " + user.getFirstName());

            long duration = System.currentTimeMillis() - startTime;

            metricsService.recordApiCall("/v1/user/self"); 
            metricsService.recordApiDuration("/v1/user/self", duration);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).headers(this.headers).body(Collections.singletonMap("Message", e.getMessage()));
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<Object> verifyUser(@RequestParam Map<String, String> queryParameter, @RequestBody(required = false) String payload, @RequestHeader(value = "isIntegrationTest", required = false) String isIntegrationTest) throws UserNotVerifiedException {
        logger.debug("The request given by the user: " + queryParameter);
        if (null != payload && !payload.isEmpty()) {
            logger.error("Payload is given");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(this.headers).build();
        }
        String message = "";
        try{
            message = userService.verifyUser(queryParameter, isIntegrationTest);
        }catch(UserNotVerifiedException ue){
            logger.error(message);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(this.headers).body(Collections.singletonMap("verificationStatus", ue.getMessage()));
        }
        logger.info("VerificationStatus for user:" + message + ", using" + queryParameter);
        return ResponseEntity.status(HttpStatus.OK).headers(this.headers).body(Collections.singletonMap("verificationStatus", message));
    }
    
    @RequestMapping(method = {RequestMethod.HEAD, RequestMethod.OPTIONS}, path = {"", "/self"})
    public ResponseEntity<Void> handleHeadOptionsCall() {
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().mustRevalidate());
        headers.setPragma("no-cache");
        headers.add("X-Content-Type-Options", "nosniff");
        
        logger.error("Wrong HTTP Method Given");

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).headers(headers).build();
    }
}