package com.example.User_service.Controller;

import com.example.User_service.DTO.LoginRequest;
import com.example.User_service.DTO.LoginResponse;
import com.example.User_service.DTO.SignupRequest;
import com.example.User_service.DTO.SignupResponse;
import com.example.User_service.DTO.UsernameResponse;
import com.example.User_service.DTO.VendorLogin;
import com.example.User_service.DTO.VendorLoginResponse;
import com.example.User_service.DTO.VendorSignupRequest;
import com.example.User_service.DTO.VendorSignupResponse;
import com.example.User_service.Service.AuthService;
import com.example.User_service.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccessController {
    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> loginRequest(
            @RequestBody final LoginRequest loginRequest,
            final HttpServletResponse response) {
        final LoginResponse loginResponse = authService.login(loginRequest);
        authService.setCookies(loginResponse, response);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/auth/vendor/login")
    public ResponseEntity<VendorLoginResponse> vendorLoginRequest(
            @RequestBody final VendorLogin loginRequest,
            final HttpServletResponse response) {
        final VendorLoginResponse loginResponse =
                authService.vendorLoginResponse(loginRequest);
        authService.setCookiesForVendor(loginResponse, response);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/auth/vendor/signup")
    public ResponseEntity<VendorSignupResponse> vendorSignupRequest(
            @RequestBody final VendorSignupRequest signupRequest)
            throws IllegalAccessException {
        final VendorSignupResponse signupResponse =
                authService.vendorSignup(signupRequest);
        return ResponseEntity.ok(signupResponse);
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<SignupResponse> signupRequest(
            @RequestBody final SignupRequest signupRequest)
            throws IllegalAccessException {
        final SignupResponse signupResponse = authService.signup(signupRequest);
        return ResponseEntity.ok(signupResponse);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<String> logout(final HttpServletResponse response) {
        authService.deleteCookies(response);
        return ResponseEntity.ok("Successfully Logout");
    }

    @GetMapping("/auth/profile")
    public ResponseEntity<UsernameResponse> getProfile(
            final HttpServletRequest request) {
        final String username = request.getHeader("X-USERNAME");
        final UsernameResponse user = userService.usernameResponse(username);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello Prometheus";
    }
}
