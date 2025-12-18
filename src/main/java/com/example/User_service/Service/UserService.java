package com.example.User_service.Service;

import com.example.User_service.DTO.UsernameResponse;
import com.example.User_service.Model.User;
import com.example.User_service.Repositery.UserRepositery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserRepositery userRepositery;



    public UsernameResponse usernameResponse(String username){
    User user=userRepositery.findByUsername(username).orElseThrow(()->new RuntimeException("User not found"));
    UsernameResponse usernameResponse=new UsernameResponse();
    usernameResponse.setUsername(user.getUsername());
    usernameResponse.setEmailId(user.getEmailId());
    usernameResponse.setPhoneNo(user.getPhoneNo());

    return  usernameResponse;


    }
}
