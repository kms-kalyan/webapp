package com.csye6225.cloud.webapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.csye6225.cloud.webapp.dto.UserRequest;
import com.csye6225.cloud.webapp.dto.UserResponse;
import com.csye6225.cloud.webapp.exception.UserNotCreatedException;
import com.csye6225.cloud.webapp.exception.UserNotFoundException;
import com.csye6225.cloud.webapp.exception.UserNotUpdatedException;
import com.csye6225.cloud.webapp.exception.UserNotVerifiedException;
import com.csye6225.cloud.webapp.model.User;
import com.csye6225.cloud.webapp.repository.UserRepository;
import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;

import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {
    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;

    //private final ModelMapper modelMapper;

    private final Environment environment;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, Environment environment) {
        this.userRepository = userRepository;
        this.environment = environment;
    }

    @Override
    public UserResponse createdUser(UserRequest user) throws UserNotCreatedException{
        Optional<User> requestedUser = userRepository.findByEmail(user.getEmail());
        if (requestedUser.isPresent()) {
            logger.error("Username already exists for user: "+user.getEmail());
            
            throw new UserNotCreatedException("Username already exists");
        }
        try {
            if (null != user.getPassword() && !user.getPassword().isBlank()) {
                user.setPassword(bcryptEncoder(user.getPassword()));
            }
            User creatingUser = new User();
            creatingUser.setToken(UUID.randomUUID().toString());
            creatingUser.setEmail(user.getEmail());
            creatingUser.setFirstName(user.getFirstName());
            creatingUser.setLastName(user.getLastName());
            creatingUser.setPassword(user.getPassword());
            User createdUser = userRepository.save(creatingUser);
            //publishMessage(createdUser.getEmail()+":"+creatingUser.getToken());
            UserResponse ur = new UserResponse();
            ur.setEmail(createdUser.getEmail());
            ur.setFirstName(createdUser.getFirstName());
            ur.setLastName(createdUser.getLastName());
            ur.setAccountCreated(createdUser.getAccountCreated());
            ur.setAccountUpdated(createdUser.getAccountUpdated());
            ur.setId(createdUser.getId());
            return ur;
        } catch (Exception e) {
            logger.error("User cannot be created for user, "+user.getEmail());
            throw new UserNotCreatedException("User cannot be created");
        }
    }

    @Override
    public UserResponse getUser(String authorization) throws UserNotFoundException, UserNotVerifiedException {
        User createdUser = getUserFromDb(authorization);

        UserResponse ur = new UserResponse();
        ur.setEmail(createdUser.getEmail());
        ur.setFirstName(createdUser.getFirstName());
        ur.setLastName(createdUser.getLastName());
        ur.setAccountCreated(createdUser.getAccountCreated());
        ur.setAccountUpdated(createdUser.getAccountUpdated());
        ur.setId(createdUser.getId());

        return ur;
    }

    @Override
    public void updateUser(UserRequest user, String authorization) throws UserNotUpdatedException, UserNotFoundException, UserNotVerifiedException {
        User userDb = getUserFromDb(authorization);
        if (null == user.getFirstName() && null == user.getLastName() && null == user.getPassword()) {
            logger.error("All fields are null or empty");
            throw new UserNotUpdatedException("All fields are null or empty");
        } else if ((null != user.getPassword() && user.getPassword().isBlank()) || (null != user.getFirstName() && user.getFirstName().isBlank()) || (null != user.getLastName() && user.getLastName().isBlank())) {
            logger.error("Empty value is given in some fields by user: "+userDb.getEmail());
            throw new UserNotUpdatedException("Empty value is given in some fields");
        }
        try {
            if (null != user.getPassword()) {
                logger.warn("New password is given");
                userDb.setPassword(bcryptEncoder(user.getPassword()));
            }
            if (null != user.getFirstName()) {
                logger.warn("New Firstname is given");
                userDb.setFirstName(user.getFirstName());
            }
            if (null != user.getLastName()) {
                logger.warn("New Lastname is given");
                userDb.setLastName(user.getLastName());
            }
            userRepository.save(userDb);
        } catch (Exception e) {
            logger.error("User not updated for user: "+userDb.getEmail());
            throw new UserNotUpdatedException("User not updated");
        }

    }

    public User getUserFromDb(String authorization) throws UserNotFoundException, UserNotVerifiedException {
        String[] usernamePassword = base64Decoder(authorization);
        if (null == usernamePassword || usernamePassword.length < 2) {
            logger.error("Username or password wrong");
            throw new UserNotFoundException("Username or password wrong");
        }
        Optional<User> requestedUser = userRepository.findByEmail(usernamePassword[0]);
        if (requestedUser.isEmpty()) {
            logger.error("User not found for user: "+usernamePassword[0]);
            throw new UserNotFoundException("User Not Found");
        }
        else if (passwordCheck(usernamePassword[1], requestedUser.get().getPassword())) {
            if(requestedUser.get().isVerified()) {
                return requestedUser.get();
            } else {
                logger.error("User not verified");
                throw new UserNotVerifiedException("User not verified");
            }
        } else {
            logger.error("Invalid Password");
            logger.error("Password is invalid for user: "+usernamePassword[0]);
            throw new UserNotFoundException("Invalid Password");
        }
    }

    @Override
    public String[] base64Decoder(String token) {
        String baseToken = token.substring(6);
        byte[] decodedBytes = Base64.getDecoder().decode(baseToken);
        String decodedString = new String(decodedBytes);
        return decodedString.split(":");
    }


    @Override
    public String bcryptEncoder(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

    @Override
    public boolean passwordCheck(String rawPassword, String hashedPassword) {
        BCryptPasswordEncoder checker = new BCryptPasswordEncoder();
        return checker.matches(rawPassword, hashedPassword);
    }

    @Override
    public String verifyUser(Map<String, String> queryParameter, String isIntegrationTest) throws UserNotVerifiedException {
        if (queryParameter.containsKey("username") && queryParameter.containsKey("token")) {
            String username = queryParameter.get("username");
            String token = queryParameter.get("token");
            Optional<User> requestedUser = userRepository.findByEmail(username);
            if (requestedUser.isEmpty()) {
                logger.error("User not verified");
                throw new UserNotVerifiedException("User Not Verified");
            } else if(null!=isIntegrationTest && isIntegrationTest.equals("true")){
                requestedUser.get().setVerified(true);
                userRepository.save(requestedUser.get());
                logger.info("User Email Verified");
                return "User Email verified";
            }
            else if(requestedUser.get().isVerified()){
                logger.info("User already verified:"+username);
                return "User already verified";
            }
            else if(null!= token && token.equals(requestedUser.get().getToken())){
                Instant instantVerificationTime = requestedUser.get().getExpiryTime().toInstant();
                logger.info("instant time: "+Instant.now()+"for user:"+requestedUser.get().getEmail());
                logger.info("db time: "+instantVerificationTime);
                Duration duration = Duration.between(instantVerificationTime, Instant.now());
                logger.info(String.valueOf(duration.toSeconds()));
                if(duration.toSeconds() < 0) {
                    requestedUser.get().setVerified(true);
                    userRepository.save(requestedUser.get());
                    logger.info("User Email Verified");
                    return "User Email verified";
                } else{
                    logger.error("Link is expired");
                    throw new UserNotVerifiedException("Link is expired");
                }
            } else{
                requestedUser.get().setVerified(false);
                userRepository.save(requestedUser.get());
                logger.error("Token is different, Given: "+token+", Actual: "+requestedUser.get().getId()+"for user: "+username);
                throw new UserNotVerifiedException("User Not Verified");
            }
        } else {
            logger.error("Error: Missing username or token");
            throw new UserNotVerifiedException("User Not Verified");
        }
    }

    public void publishMessage(String usernameToken) throws InterruptedException {
        String projectId = environment.getProperty("PROJECT_ID");
        String topicId = environment.getProperty("TOPIC_ID");
        logger.info("In Publish Message for usernameToken: "+usernameToken);
        TopicName topicName = TopicName.of(projectId, topicId);
        logger.info(topicName.toString());

        Publisher publisher = null;
        try {
            publisher = Publisher.newBuilder(topicName).build();

            ByteString data = ByteString.copyFromUtf8(usernameToken);
            PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

            ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
            logger.info("After publish"+messageIdFuture);
            String messageId = messageIdFuture.get();
            logger.info("Published message ID: " + messageId + ", for user: "+usernameToken);
        } catch (Exception e){
            logger.error("Error in publishing message to send email for user: "+usernameToken);
        }
            finally {
            logger.info("In finally publish method");
            if (publisher != null) {
                logger.info(publisher.getTopicName().toString());
                // When finished with the publisher, shutdown to free up resources.
                publisher.shutdown();
                publisher.awaitTermination(1, TimeUnit.MINUTES);
            }
        }
    }


}