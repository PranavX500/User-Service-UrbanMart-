package com.example.User_service.Service;

import com.example.User_service.DTO.*;
import com.example.User_service.Model.Role;
import com.example.User_service.Model.Vendor;
import com.example.User_service.Repositery.UserRepositery;
import com.example.User_service.Model.User;
import com.example.User_service.Repositery.VendorRepositery;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
    private final VendorRepositery vendorRepositery;


    public LoginResponse login(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid username or password");
        }

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = authUtil.generateAccessTokenforuser(user);
        return new LoginResponse(token, user.getId());
    }


    public VendorLoginResponse vendorLoginResponse(VendorLogin loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getVendorName(),
                        loginRequest.getPassword()
                )
        );


        Vendor vendor = vendorRepositery.findByVendorName(loginRequest.getVendorName())
                .orElseThrow(() -> new RuntimeException("vendor not found"));

        String token = authUtil.generateAccessTokenforvendor(vendor);
        return new VendorLoginResponse (token, vendor.getId());
    }


    public VendorSignupResponse vendorSignup(VendorSignupRequest vendorSignupRequest) throws IllegalAccessException {
    if(vendorRepositery.findByVendorName(vendorSignupRequest.getVendorName()).isPresent()){
        throw new IllegalAccessException("Vendor already exists");
    }
    Vendor vendor=Vendor.builder()
            .vendorName(vendorSignupRequest.getVendorName())
            .phoneNo(vendorSignupRequest.getPhoneNo())
            .emailId(vendorSignupRequest.getEmailId())
            .password(passwordEncoder.encode(vendorSignupRequest.getPassword()))
            .verify(false)
            .role(Role.valueOf("ROLE_VENDOR"))
            .build();
        OtpRequest otpRequest=new OtpRequest();
        otpRequest.setEmailId(vendorSignupRequest.getEmailId());
        otpRequest.setRole(String.valueOf(Role.valueOf("ROLE_VENDOR")));
       vendorRepositery.save(vendor);
        emailProducer.sendEmailId(otpRequest);
       return new VendorSignupResponse(vendor.getId(), vendor.getVendorName());
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
                .role(Role.valueOf("ROLE_USER"))
                .build();
        OtpRequest otpRequest=new OtpRequest();
        otpRequest.setEmailId(signupRequest.getEmailId());
        otpRequest.setRole(String.valueOf(Role.ROLE_USER));
        userRepository.save(user);


        emailProducer.sendEmailId(otpRequest);


        return new SignupResponse(user.getId(), user.getUsername());
    }

    public void SetCookiesforvendor(VendorLoginResponse loginResponse, HttpServletResponse response) {

        ResponseCookie cookie = ResponseCookie.from("token", loginResponse.getJwt())
                .httpOnly(true)
                .secure(false)     // ⭐ MUST be false for same-site HTTP
                .path("/")
                .sameSite("Lax")   // ⭐ MUST be Lax because Chrome marks it same-site
                .maxAge(604800)
                .build();;
        System.out.println(cookie);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void SetCookies(LoginResponse loginResponse, HttpServletResponse response) {

        ResponseCookie cookie = ResponseCookie.from("token", loginResponse.getJwt())
                .httpOnly(true)
                .secure(false)     // ⭐ MUST be false for same-site HTTP
                .path("/")
                .sameSite("Lax")   // ⭐ MUST be Lax because Chrome marks it same-site
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
