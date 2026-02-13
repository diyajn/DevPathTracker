package com.org.example.service;

import com.org.example.entities.RefreshToken;
import com.org.example.entities.User;
import com.org.example.repository.RefreshTokenRepository;
import com.org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Autowired
    private RefreshTokenRepository tokenRepository;
    @Autowired
    private UserRepository userRepository;

    public long refreshTokenValidity=2*60*1000; //5hr ki validity

    //create refresh token
    public RefreshToken createRefreshToken(String username){
        User user =userRepository.findByUsername(username).get();
        RefreshToken refreshToken=user.getRefreshToken();
        if(refreshToken==null){
            //create...
            refreshToken=RefreshToken.builder()
                    .refreshToken(UUID.randomUUID().toString())
                    .expire(Instant.now().plusMillis(refreshTokenValidity))
                    .user(userRepository.findByUsername(username).get())   //set user in refreshtoken
                    .build();
        }else{
            //increase expiry time only
            refreshToken.setExpire(Instant.now().plusMillis(refreshTokenValidity));
        }
        //now add refreshtoken to user
         user.setRefreshToken(refreshToken);
        //save to db
        tokenRepository.save(refreshToken);
        return refreshToken;
    }

    //verify refresh token
    public RefreshToken verifyRefreshToken(String refreshToken){
        RefreshToken refreshTokenObject=tokenRepository.findByRefreshToken(refreshToken).orElseThrow(()-> new RuntimeException("Given token is not exist in db"));
        if(refreshTokenObject.getExpire().compareTo(Instant.now())<0){
            System.out.println("Refresh Token Expired!!");
            //delete from db if expiry
            tokenRepository.delete(refreshTokenObject);
            throw new RuntimeException("Refresh Token Expired!!");
        }
        return refreshTokenObject;
    }
}
