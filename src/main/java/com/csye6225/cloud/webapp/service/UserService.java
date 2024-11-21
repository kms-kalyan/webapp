package com.csye6225.cloud.webapp.service;

import org.springframework.stereotype.Service;

import com.csye6225.cloud.webapp.dto.UserRequest;
import com.csye6225.cloud.webapp.dto.UserResponse;
import com.csye6225.cloud.webapp.exception.UserNotCreatedException;
import com.csye6225.cloud.webapp.exception.UserNotFoundException;
import com.csye6225.cloud.webapp.exception.UserNotUpdatedException;
import com.csye6225.cloud.webapp.exception.UserNotVerifiedException;

import java.util.Map;

@Service
public interface UserService {
    UserResponse createdUser(UserRequest user) throws UserNotCreatedException;
    UserResponse getUser(String authorization) throws UserNotFoundException, UserNotVerifiedException;
    String updateUser(UserRequest user, String authorization) throws UserNotUpdatedException, UserNotFoundException, UserNotVerifiedException;
    String[] base64Decoder(String token);
    String bcryptEncoder(String password);
    boolean passwordCheck(String rawPassword, String hashedPassword);
    String verifyUser(Map<String, String> queryParameter, String isIntegrationTest) throws UserNotVerifiedException;
}