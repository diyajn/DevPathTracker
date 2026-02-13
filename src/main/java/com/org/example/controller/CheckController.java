package com.org.example.controller;

import com.org.example.entities.User;
import com.org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
public class CheckController {

   @Autowired
   private UserService userService;

    @GetMapping("/check")
    public String handler(){
        return "Dev Path Tracker working fine";
    }

    @GetMapping("/current_user")
    public String getLoggedInUser(Principal principal){
        return principal.getName();
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUser(){
        List<User> users= userService.getAllUser();
        if(users.size()<=0){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(users);
    }
}
