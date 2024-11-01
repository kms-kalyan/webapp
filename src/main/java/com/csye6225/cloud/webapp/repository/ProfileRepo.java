package com.csye6225.cloud.webapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.csye6225.cloud.webapp.model.UserImage;

public interface ProfileRepo extends JpaRepository<UserImage, Long> {

    Optional<UserImage> findByUserId(String userId);
}
