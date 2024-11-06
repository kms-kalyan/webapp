package com.csye6225.cloud.webapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.csye6225.cloud.webapp.exception.UserNotFoundException;
import com.csye6225.cloud.webapp.model.User;
import com.csye6225.cloud.webapp.model.UserImage;
import com.csye6225.cloud.webapp.repository.ProfileRepo;
import com.csye6225.cloud.webapp.service.UserServiceImpl;
import com.csye6225.cloud.webapp.service.MetricsService; 

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/v1/user/self")
public class ProfilePictureController {

    private final Logger logger = LoggerFactory.getLogger(ProfilePictureController.class);

    @Autowired
    private S3Client s3Client;

    @Autowired
    private ProfileRepo profileRepo;

    @Autowired
    private MetricsService metricsService;

    @Value("${aws.s3.bucket_name}")
    private String bucketName; 

    @Autowired
    UserServiceImpl userServiceImpl;

    @PostMapping(path="/pic", consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity<Object> uploadProfilePic(@RequestParam MultipartFile profilePic, 
                                                   @RequestHeader(name = "Authorization", required = true) String authorization) throws IOException {
        long startTime = System.currentTimeMillis();     

        String email;
        try {
            email = extractEmailFromAuthorizationHeader(authorization);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;     
            metricsService.recordApiCall("/v1/user/self/pic");     
            metricsService.recordApiDuration("/v1/user/self/pic", duration);     

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .header("Cache-Control", "no-cache")
                                 .build();
        }

        if (profilePic.isEmpty()) {
            long duration = System.currentTimeMillis() - startTime;     
            metricsService.recordApiCall("/v1/user/self/pic");     
            metricsService.recordApiDuration("/v1/user/self/pic", duration);     

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is empty");
        }

        String fileName = profilePic.getOriginalFilename();
        String key = email + "/" + fileName; 

        byte[] fileBytes = profilePic.getBytes();
        
        s3Client.putObject(PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build(),
            RequestBody.fromBytes(fileBytes));  

        UserImage userImage = new UserImage();
        userImage.setFileName(fileName);
        userImage.setUrl(bucketName + "/" + key);
        userImage.setUploadDate(LocalDateTime.now());
        userImage.setUserId(email);

        profileRepo.save(userImage);

        logger.info("Profile picture uploaded successfully for user: " + email);

        long duration = System.currentTimeMillis() - startTime;     

        metricsService.recordApiCall("/v1/user/self/pic");     
        metricsService.recordApiDuration("/v1/user/self/pic", duration);     

        return ResponseEntity.status(HttpStatus.CREATED).body(userImage);
    }

    @GetMapping("/pic")
    public ResponseEntity<Object> getProfilePic(@RequestHeader("authorization") String authorization) {
        long startTime = System.currentTimeMillis();     

        String email;
        try {
            email = extractEmailFromAuthorizationHeader(authorization);
        } catch (UserNotFoundException e) {
            long duration = System.currentTimeMillis() - startTime;     
            metricsService.recordApiCall("/v1/user/self/pic");     
            metricsService.recordApiDuration("/v1/user/self/pic", duration);     

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .header("Cache-Control", "no-cache")
                                 .build();
        }

        Optional<UserImage> userImageOptional = profileRepo.findByUserId(email);

        if (userImageOptional.isPresent()) {
            UserImage userImage = userImageOptional.get();
            logger.info("Profile picture fetched successfully for user: " + email);

            long duration = System.currentTimeMillis() - startTime;     

            metricsService.recordApiCall("/v1/user/self/pic");     
            metricsService.recordApiDuration("/v1/user/self/pic", duration);     

            return ResponseEntity.status(HttpStatus.OK).body(userImage);
        } else {
            logger.warn("No profile picture found for user: " + email);

            long duration = System.currentTimeMillis() - startTime;     

            metricsService.recordApiCall("/v1/user/self/pic");     
            metricsService.recordApiDuration("/v1/user/self/pic", duration);     

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    
    @DeleteMapping("/pic")
    public ResponseEntity<Object> deleteProfilePic(@RequestHeader("authorization") String authorization) {
        long startTime = System.currentTimeMillis();     

        String email;
        try {
            email = extractEmailFromAuthorizationHeader(authorization);
        } catch (UserNotFoundException e) {
            long duration = System.currentTimeMillis() - startTime;     
            metricsService.recordApiCall("/v1/user/self/pic");     
            metricsService.recordApiDuration("/v1/user/self/pic", duration);     

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .header("Cache-Control", "no-cache")
                                 .build();
        }

        Optional<UserImage> userImageOptional = profileRepo.findByUserId(email);

        if (userImageOptional.isPresent()) {
            UserImage userImage = userImageOptional.get();
            String key = email + "/" + userImage.getFileName();

            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());

            profileRepo.delete(userImage);

            logger.info("Profile picture deleted successfully for user: " + email);

            long duration = System.currentTimeMillis() - startTime;

            metricsService.recordApiCall("/v1/user/self/pic");
            metricsService.recordApiDuration("/v1/user/self/pic", duration);

            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            logger.warn("No profile picture found for user: " + email);

            long duration = System.currentTimeMillis() - startTime;

            metricsService.recordApiCall("/v1/user/self/pic");
            metricsService.recordApiDuration("/v1/user/self/pic", duration);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile picture not found");
        }
    }

    private String extractEmailFromAuthorizationHeader(String authorizationHeader) throws UserNotFoundException{
        
        User user = userServiceImpl.getUserFromDb(authorizationHeader);
        return user.getEmail(); 
    }
}