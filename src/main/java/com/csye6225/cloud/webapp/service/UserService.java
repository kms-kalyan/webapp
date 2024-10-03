package com.csye6225.cloud.webapp.service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.csye6225.cloud.webapp.model.User;
import com.csye6225.cloud.webapp.repository.UserDAO;

@Service
@Transactional
public class UserService{

    @Autowired
    private UserDAO userDao;

    public User createUser(User user) {
        try{
            if (userDao.findByEmail(user.getEmail()) != null) {
                throw new BadRequestException("User already exists");
            }
        }catch(BadRequestException e){
            System.err.println(e.getMessage());
            return null;
        }
        String uuid = UUID.randomUUID().toString();
        while(isIdPresent(uuid)){
            uuid = UUID.randomUUID().toString();
        }
        user.setId(uuid);
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        user.setAccountCreated(LocalDateTime.now());
        user.setAccountUpdated(LocalDateTime.now());
        userDao.save(user);
        return user;
    }

    public User updateUser(User user, String email) {
        User existingUser = userDao.findByEmail(email);
        if (existingUser == null) {
            return null;
        }
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        if (user.getPassword() != null) {
            existingUser.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        }
        existingUser.setAccountUpdated(LocalDateTime.now());
        userDao.save(existingUser);
        return existingUser;
    }

    public User getUserByEmail(String email) {
        User user = userDao.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        return user;
    }

    public boolean isIdPresent(String id) {
        User user = userDao.findById(id);
        if (user == null) {
            return true;
        }
        return false;
    }
}
