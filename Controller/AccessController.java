package com.example.User_service.Controller;

import com.example.User_service.DTO.*;
import com.example.User_service.Model.User;
import com.example.User_service.Repositery.UserRepositery;
import com.example.User_service.Service.AuthService;
import com.example.User_service.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController

public class AccessController {
    @Autowired
    AuthService authService;
    @Autowired
    UserService userService;


    @PostMapping("/auth/login")
    public ResponseEntity<?> loginRequest(@RequestBody LoginRequest loginRequest, HttpServletResponse response){
        LoginResponse loginResponse=authService.login(loginRequest);
       authService.SetCookies(loginResponse,response );
        System.out.println(loginResponse);
       return ResponseEntity.ok(loginResponse);
    }
    @PostMapping("/auth/signup")
    public ResponseEntity<?>signupRequest(@RequestBody SignupRequest signupRequest) throws IllegalAccessException {
        SignupResponse signupResponse=authService.signup(signupRequest);

        return ResponseEntity.ok(signupResponse);
    }
    @PostMapping  ("/auth/Logout")
    public ResponseEntity<?>Logout(HttpServletResponse response){
        authService.DeleteCookies(response);
        return ResponseEntity.ok("Successfully Logout");
    }
     @GetMapping("/auth/profile")
    public ResponseEntity<UsernameResponse> getProfile(HttpServletRequest request) {

        String userId = request.getHeader("X-USER-ID");
        String username = request.getHeader("X-USERNAME");


        UsernameResponse user = userService. usernameResponse(username);

        return ResponseEntity.ok(user);
    }
  
}
