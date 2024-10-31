package com.csye6225.cloud.webapp.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Updaterequest {
    private String firstName;
    private String lastName;
    private String password;
}

