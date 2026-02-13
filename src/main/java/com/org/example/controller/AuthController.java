package com.org.example.controller;

import com.org.example.config.JwtHelper;
import com.org.example.entities.RefreshToken;
import com.org.example.entities.User;
import com.org.example.payload.JwtRequest;
import com.org.example.payload.JwtResponse;
import com.org.example.payload.RefreshTokenRequest;
import com.org.example.service.AuthService;
import com.org.example.service.RefreshTokenService;
import com.org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtHelper jwtHelper;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest jwtRequest) {
        return ResponseEntity.ok(authService.login(jwtRequest.getUsername(), jwtRequest.getPassword()));
    }

    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody JwtRequest jwtRequest) {
        User createdUser = authService.signup(jwtRequest.getUsername(), jwtRequest.getPassword());
        return ResponseEntity.ok(createdUser);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<JwtResponse> refreshJwtToken(@RequestBody RefreshTokenRequest refreshTokenRequest){
        RefreshToken refreshToken=refreshTokenService.verifyRefreshToken(refreshTokenRequest.getRefreshToken());
        User user=refreshToken.getUser();
        String token= jwtHelper.generateToken(user);
        return ResponseEntity.ok(new JwtResponse(token,refreshToken.getRefreshToken()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credentials Invalid");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntime(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

}












