package com.example.User_service.Service;

import com.example.User_service.DTO.LoginRequest;
import com.example.User_service.DTO.LoginResponse;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AuthUtil authUtil;

    @Mock
    private UserRepositery userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailConsumer emailConsumer;

    @Mock
    private EmailProducer emailProducer;

    @Mock
    private VendorRepositery vendorRepositery;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthService authService;

    @Test
    void loginAuthenticatesUserAndReturnsJwt() {
        LoginRequest request = new LoginRequest("alice", "secret");
        User user = User.builder()
                .id(10L)
                .username("alice")
                .emailId("alice@example.com")
                .role(Role.ROLE_USER)
                .build();

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(authUtil.generateAccessTokenforuser(user)).thenReturn("jwt-token");

        LoginResponse result = authService.login(request);

        assertThat(result.getJwt()).isEqualTo("jwt-token");
        assertThat(result.getId()).isEqualTo(10L);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(authUtil).generateAccessTokenforuser(user);
    }

    @Test
    void loginThrowsRuntimeExceptionForInvalidCredentials() {
        LoginRequest request = new LoginRequest("alice", "wrong");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("bad credentials"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(request));

        assertThat(exception.getMessage()).isEqualTo("Invalid username or password");
        verify(userRepository, never()).findByUsername(any());
    }

    @Test
    void vendorLoginAuthenticatesVendorAndReturnsJwt() {
        VendorLogin request = new VendorLogin("vendor-a", "secret");
        Vendor vendor = Vendor.builder()
                .Id(21L)
                .vendorName("vendor-a")
                .emailId("vendor@example.com")
                .role(Role.ROLE_VENDOR)
                .build();

        when(vendorRepositery.findByVendorName("vendor-a")).thenReturn(Optional.of(vendor));
        when(authUtil.generateAccessTokenforvendor(vendor)).thenReturn("vendor-jwt");

        VendorLoginResponse result = authService.vendorLoginResponse(request);

        assertThat(result.getJwt()).isEqualTo("vendor-jwt");
        assertThat(result.getId()).isEqualTo(21L);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void signupPersistsEncodedUserAndPublishesOtpRequest() throws IllegalAccessException {
        SignupRequest request = new SignupRequest("alice", "secret", "9999999999", "alice@example.com");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret")).thenReturn("encoded-secret");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture())).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(15L);
            return saved;
        });

        SignupResponse result = authService.signup(request);

        assertThat(result.getId()).isEqualTo(15L);
        assertThat(result.getUsername()).isEqualTo("alice");
        assertThat(userCaptor.getValue().getPassword()).isEqualTo("encoded-secret");
        assertThat(userCaptor.getValue().getRole()).isEqualTo(Role.ROLE_USER);
        assertThat(userCaptor.getValue().isVerify()).isFalse();

        ArgumentCaptor<com.example.User_service.DTO.OtpRequest> otpCaptor =
                ArgumentCaptor.forClass(com.example.User_service.DTO.OtpRequest.class);
        verify(emailProducer).sendEmailId(otpCaptor.capture());
        assertThat(otpCaptor.getValue().getEmailId()).isEqualTo("alice@example.com");
        assertThat(otpCaptor.getValue().getRole()).isEqualTo("ROLE_USER");
    }

    @Test
    void signupRejectsExistingUser() {
        SignupRequest request = new SignupRequest("alice", "secret", "9999999999", "alice@example.com");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(User.builder().username("alice").build()));

        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> authService.signup(request));

        assertThat(exception.getMessage()).isEqualTo("User already exists");
        verify(userRepository, never()).save(any());
        verify(emailProducer, never()).sendEmailId(any());
    }

    @Test
    void vendorSignupPersistsEncodedVendorAndPublishesOtpRequest() throws IllegalAccessException {
        VendorSignupRequest request = new VendorSignupRequest("vendor-a", "secret", "vendor@example.com", "8888888888");
        when(vendorRepositery.findByVendorName("vendor-a")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret")).thenReturn("encoded-secret");

        ArgumentCaptor<Vendor> vendorCaptor = ArgumentCaptor.forClass(Vendor.class);
        when(vendorRepositery.save(vendorCaptor.capture())).thenAnswer(invocation -> {
            Vendor saved = invocation.getArgument(0);
            saved.setId(30L);
            return saved;
        });

        VendorSignupResponse result = authService.vendorSignup(request);

        assertThat(result.getId()).isEqualTo(30L);
        assertThat(result.getVendorName()).isEqualTo("vendor-a");
        assertThat(vendorCaptor.getValue().getPassword()).isEqualTo("encoded-secret");
        assertThat(vendorCaptor.getValue().getRole()).isEqualTo(Role.ROLE_VENDOR);
        assertThat(vendorCaptor.getValue().isVerify()).isFalse();
        verify(emailProducer).sendEmailId(any());
    }

    @Test
    void setCookiesAddsJwtHeader() {
        authService.SetCookies(new LoginResponse("jwt-token", 10L), response);

        ArgumentCaptor<String> headerValue = ArgumentCaptor.forClass(String.class);
        verify(response).addHeader(eq(org.springframework.http.HttpHeaders.SET_COOKIE), headerValue.capture());
        assertThat(headerValue.getValue()).contains("token=jwt-token");
        assertThat(headerValue.getValue()).contains("HttpOnly");
    }

    @Test
    void setVendorCookiesAddsJwtHeader() {
        authService.SetCookiesforvendor(new VendorLoginResponse("vendor-jwt", 21L), response);

        ArgumentCaptor<String> headerValue = ArgumentCaptor.forClass(String.class);
        verify(response).addHeader(eq(HttpHeaders.SET_COOKIE), headerValue.capture());
        assertThat(headerValue.getValue()).contains("token=vendor-jwt");
        assertThat(headerValue.getValue()).contains("SameSite=Lax");
    }

    @Test
    void deleteCookiesAddsExpiredCookie() {
        authService.DeleteCookies(response);

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(cookieCaptor.capture());
        assertThat(cookieCaptor.getValue().getName()).isEqualTo("token");
        assertThat(cookieCaptor.getValue().getMaxAge()).isZero();
    }
}
