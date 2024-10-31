package com.csye6225.cloud.webapp.model;


import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
public class UserImage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private Long id;
    @Getter
    @Setter
    private String fileName;
    @Getter
    @Setter
    private String url;
    @Getter
    @Setter
    private LocalDateTime uploadDate;
    @Getter
    @Setter
    private String userId;  // Email of the user who uploaded the image

}