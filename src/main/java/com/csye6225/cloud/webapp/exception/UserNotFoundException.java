package com.csye6225.cloud.webapp.exception;

public class UserNotFoundException extends Exception{
    public UserNotFoundException(String message){
        super(message);
    }
}