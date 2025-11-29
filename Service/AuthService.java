package com.example.User_service.Service;

import com.example.User_service.DTO.*;
import com.example.User_service.Repositery.UserRepositery;
import com.example.User_service.Model.User;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AuthUtil authUtil;
    private final UserRepositery userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailConsumer emailConsumer;
    private final EmailProducer emailProducer;


    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = authUtil.generateAccessToken(user);
        return new LoginResponse(token, user.getId());
    }

    public SignupResponse signup(SignupRequest signupRequest) throws IllegalAccessException {
        if (userRepository.findByUsername(signupRequest.getUsername()).isPresent()) {
            throw new IllegalAccessException("User already exists");
        }

        User user = User.builder()
                .username(signupRequest.getUsername())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .phoneNo(signupRequest.getPhoneNo())
                .emailId(signupRequest.getEmailId())
                .verify(false)
                .role("ROLE_USER")
                .build();
        OtpRequest otpRequest=new OtpRequest();
        otpRequest.setEmailId(signupRequest.getEmailId());
        userRepository.save(user);
        emailProducer.sendEmailId(otpRequest);




        return new SignupResponse(user.getId(), user.getUsername());
    }
    public void SetCookies(LoginResponse loginResponse, HttpServletResponse response) {

        ResponseCookie cookie = ResponseCookie.from("token", loginResponse.getJwt())
                .httpOnly(true)
                .secure(false)     
                .path("/")
                .sameSite("Lax")  
                .maxAge(604800)
                .build();;
        System.out.println(cookie);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void DeleteCookies(HttpServletResponse response){
        Cookie cookie = new Cookie("token",null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
    }

}
