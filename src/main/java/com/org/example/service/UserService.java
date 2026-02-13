package com.org.example.service;

import com.org.example.entities.User;
import com.org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    //get all user
    public List<User> getAllUser(){
        return userRepository.findAll();
    }

    //check user
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    //create user
    public User createUser(User user){
        return userRepository.save(user);
    }
}