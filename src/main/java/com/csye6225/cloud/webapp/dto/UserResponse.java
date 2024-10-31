package com.csye6225.cloud.webapp.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserResponse {
    @Getter
    @Setter
    private String id;
    @Getter
    @Setter
    private String firstName;
    @Getter
    @Setter
    private String lastName;
    @Getter
    @Setter
    private String email;
    @Getter
    @Setter
    private LocalDateTime accountCreated;
    @Getter
    @Setter
    private LocalDateTime accountUpdated;
}
