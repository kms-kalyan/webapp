package com.csye6225.cloud.webapp.model;

import java.time.LocalDateTime;
import java.util.Date;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Value;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "users")
public class User {

    @Id
    @Getter
    @Setter
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    private String id;

    @Column(unique = true, nullable = false)
    @Getter
    @Setter
    private String email;

    @Column(nullable = false)
    @Getter
    @Setter
    private String password;

    @Column(name = "first_name")
    @Getter
    @Setter
    private String firstName;

    @Column(name = "last_name")
    @Getter
    @Setter
    private String lastName;

    @Column(name = "account_created")
    @Getter
    @Setter
    private LocalDateTime accountCreated;

    @Column(name = "account_updated")
    @Getter
    @Setter
    private LocalDateTime accountUpdated;

    @Getter
    @Setter
    @Column(name = "IS_VERIFIED")
    @Value("${props.boolean.isVerified:#{false}}")
    private boolean isVerified;

    @Column(name = "TOKEN")
    @Getter
    @Setter
    private String token;

    @Column(name = "EXPIRY_TIME")
    @Getter
    @Setter
    private Date expiryTime;
}
