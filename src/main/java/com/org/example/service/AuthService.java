package com.org.example.service;

import com.org.example.config.JwtHelper;
import com.org.example.entities.RefreshToken;
import com.org.example.entities.User;
import com.org.example.dto.JwtResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public JwtResponse login(String username, String password) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid Username or Password");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String token=jwtHelper.generateToken(userDetails);
        RefreshToken refreshToken=refreshTokenService.createRefreshToken(username);
        return new JwtResponse(token, refreshToken.getRefreshToken());
    }

    public User signup(String username, String password) {
        // Check if username already exists
        if (userService.existsByUsername(username)) {
            throw new RuntimeException("Username already taken");
        }
       //create new user
        User user=new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        return userService.createUser(user);
    }

}
