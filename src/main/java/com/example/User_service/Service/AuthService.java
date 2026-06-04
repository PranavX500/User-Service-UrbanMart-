package com.example.User_service.Service;

import com.example.User_service.DTO.LoginRequest;
import com.example.User_service.DTO.LoginResponse;
import com.example.User_service.DTO.OtpRequest;
import com.example.User_service.DTO.SignupRequest;
import com.example.User_service.DTO.SignupResponse;
import com.example.User_service.DTO.VendorLogin;
import com.example.User_service.DTO.VendorLoginResponse;
import com.example.User_service.DTO.VendorSignupRequest;
import com.example.User_service.DTO.VendorSignupResponse;
import com.example.User_service.Model.Role;
import com.example.User_service.Model.User;
import com.example.User_service.Model.Vendor;
import com.example.User_service.Repositery.UserRepositery;
import com.example.User_service.Repositery.VendorRepositery;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final int COOKIE_EXPIRY_SECONDS = 604800;

    private final AuthenticationManager authenticationManager;
    private final AuthUtil authUtil;
    private final UserRepositery userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailProducer emailProducer;
    private final VendorRepositery vendorRepositery;
    private final UserMetrics userMetrics;

    public LoginResponse login(final LoginRequest loginRequest) {
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

        final User user = userRepository
                .findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        final String token = authUtil.generateAccessTokenForUser(user);
        return new LoginResponse(token, user.getId());
    }

    public VendorLoginResponse vendorLoginResponse(
            final VendorLogin loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getVendorName(),
                        loginRequest.getPassword()
                )
        );

        final Vendor vendor = vendorRepositery
                .findByVendorName(loginRequest.getVendorName())
                .orElseThrow(() -> new RuntimeException("vendor not found"));

        final String token = authUtil.generateAccessTokenForVendor(vendor);
        return new VendorLoginResponse(token, vendor.getId());
    }

    public VendorSignupResponse vendorSignup(
            final VendorSignupRequest vendorSignupRequest)
            throws IllegalAccessException {
        if (vendorRepositery.findByVendorName(
                vendorSignupRequest.getVendorName()).isPresent()) {
            throw new IllegalAccessException("Vendor already exists");
        }

        final Vendor vendor = Vendor.builder()
                .vendorName(vendorSignupRequest.getVendorName())
                .phoneNo(vendorSignupRequest.getPhoneNo())
                .emailId(vendorSignupRequest.getEmailId())
                .password(passwordEncoder.encode(
                        vendorSignupRequest.getPassword()))
                .verify(false)
                .role(Role.ROLE_VENDOR)
                .build();
        final OtpRequest otpRequest = new OtpRequest();
        otpRequest.setEmailId(vendorSignupRequest.getEmailId());
        otpRequest.setRole(String.valueOf(Role.ROLE_VENDOR));
        vendorRepositery.save(vendor);
        emailProducer.sendEmailId(otpRequest);
        return new VendorSignupResponse(
                vendor.getId(),
                vendor.getVendorName());
    }

    public SignupResponse signup(final SignupRequest signupRequest)
            throws IllegalAccessException {
        if (userRepository.findByUsername(
                signupRequest.getUsername()).isPresent()) {
            throw new IllegalAccessException("User already exists");
        }

        final User user = User.builder()
                .username(signupRequest.getUsername())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .phoneNo(signupRequest.getPhoneNo())
                .emailId(signupRequest.getEmailId())
                .verify(false)
                .role(Role.ROLE_USER)
                .build();
        final OtpRequest otpRequest = new OtpRequest();
        otpRequest.setEmailId(signupRequest.getEmailId());
        otpRequest.setRole(String.valueOf(Role.ROLE_USER));
        userRepository.save(user);
        emailProducer.sendEmailId(otpRequest);
        userMetrics.incrementUserRegistration();
        return new SignupResponse(user.getId(), user.getUsername());
    }

    public void setCookiesForVendor(
            final VendorLoginResponse loginResponse,
            final HttpServletResponse response) {
        final ResponseCookie cookie =
                ResponseCookie.from("token", loginResponse.getJwt())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(COOKIE_EXPIRY_SECONDS)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void setCookies(
            final LoginResponse loginResponse,
            final HttpServletResponse response) {
        final ResponseCookie cookie =
                ResponseCookie.from("token", loginResponse.getJwt())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(COOKIE_EXPIRY_SECONDS)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void deleteCookies(final HttpServletResponse response) {
        final Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
