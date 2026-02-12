package com.org.example.controller;

import com.org.example.config.JwtHelper;
import com.org.example.payload.JwtRequest;
import com.org.example.payload.JwtResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtHelper jwtHelper;


    //login(Generate Token)
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> generateToken(@RequestBody JwtRequest jwtRequest){
        //check username and password is correct or not
        try{
             authenticationManager.authenticate(
                     new UsernamePasswordAuthenticationToken(
                             jwtRequest.getUsername(),
                             jwtRequest.getPassword()
                     )
             );
        }catch(BadCredentialsException e){
            throw new BadCredentialsException("Invalid Username and password");
        }

        //To generate Jwt token
        UserDetails userDetails=userDetailsService.loadUserByUsername(jwtRequest.getUsername());

        //create jwt
        String token= jwtHelper.generateToken(userDetails);

        //send token in response
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public String ExceptionHAndler(){
        return "Credentials Invalid";
    }

}
