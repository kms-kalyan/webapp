package com.csye6225.cloud.webapp.service;

import java.time.LocalDateTime;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        user.setAccountCreated(LocalDateTime.now());
        user.setAccountUpdated(LocalDateTime.now());
        userDao.save(user);
        return user;
    }

    public User updateUser(User updatedUser, String email) throws BadRequestException {
        User existingUser = userDao.findByEmail(email);
    if (existingUser == null) {
        throw new ResourceNotFoundException("User not found");
    }

    // Update fields
    existingUser.setFirstName(updatedUser.getFirstName());
    existingUser.setLastName(updatedUser.getLastName());
    existingUser.setPassword(BCrypt.hashpw(updatedUser.getPassword(), BCrypt.gensalt()));
    existingUser.setAccountUpdated(LocalDateTime.now());

    // Use merge instead of persist
    return userDao.merge(existingUser);
    }

    public boolean checkPassword(User user, String rawPassword) {
        return BCrypt.checkpw(rawPassword, user.getPassword());
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
            return false;
        }
        return true;
    }
}
