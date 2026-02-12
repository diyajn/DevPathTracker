package com.org.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class CheckController {


    @GetMapping("/check")
    public String handler(){
        return "Dev Path Tracker working fine";
    }

    @GetMapping("/current_user")
    public String getLoggedInUser(Principal principal){
        return principal.getName();
    }

}
