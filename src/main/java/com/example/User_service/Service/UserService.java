package com.example.User_service.Service;

import com.example.User_service.DTO.UsernameResponse;
import com.example.User_service.Model.User;
import com.example.User_service.Repositery.UserRepositery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepositery userRepositery;

    public UsernameResponse usernameResponse(final String username) {
        final User user = userRepositery.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        final UsernameResponse usernameResponse = new UsernameResponse();
        usernameResponse.setUsername(user.getUsername());
        usernameResponse.setEmailId(user.getEmailId());
        usernameResponse.setPhoneNo(user.getPhoneNo());
        return usernameResponse;
    }
}
