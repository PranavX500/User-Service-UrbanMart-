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
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccessController.class)
@AutoConfigureMockMvc(addFilters = false)
class AccessControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserService userService;

    @Test
    void loginReturnsResponseBodyAndDelegatesCookieCreation() throws Exception {
        LoginRequest request = new LoginRequest("alice", "secret");
        LoginResponse response = new LoginResponse("jwt-token", 11L);
        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value("jwt-token"))
                .andExpect(jsonPath("$.id").value(11L));

        ArgumentCaptor<LoginRequest> requestCaptor = ArgumentCaptor.forClass(LoginRequest.class);
        verify(authService).login(requestCaptor.capture());
        assertThat(requestCaptor.getValue().getUsername()).isEqualTo("alice");
        assertThat(requestCaptor.getValue().getPassword()).isEqualTo("secret");
        verify(authService).setCookies(
                any(LoginResponse.class),
                any(HttpServletResponse.class));
    }

    @Test
    void vendorLoginReturnsResponseBodyAndDelegatesCookieCreation() throws Exception {
        VendorLogin request = new VendorLogin("vendor-a", "secret");
        VendorLoginResponse response = new VendorLoginResponse("vendor-jwt", 7L);
        when(authService.vendorLoginResponse(any(VendorLogin.class))).thenReturn(response);

        mockMvc.perform(post("/auth/vendor/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value("vendor-jwt"))
                .andExpect(jsonPath("$.id").value(7L));

        verify(authService).vendorLoginResponse(any(VendorLogin.class));
        verify(authService).setCookiesForVendor(
                any(VendorLoginResponse.class),
                any(HttpServletResponse.class));
    }

    @Test
    void signupReturnsCreatedUserDetails() throws Exception {
        SignupRequest request = new SignupRequest("alice", "secret", "9999999999", "alice@example.com");
        when(authService.signup(any(SignupRequest.class))).thenReturn(new SignupResponse(15L, "alice"));

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(15L))
                .andExpect(jsonPath("$.username").value("alice"));
    }

    @Test
    void vendorSignupReturnsCreatedVendorDetails() throws Exception {
        VendorSignupRequest request = new VendorSignupRequest("vendor-a", "secret", "vendor@example.com", "8888888888");
        when(authService.vendorSignup(any(VendorSignupRequest.class)))
                .thenReturn(new VendorSignupResponse(20L, "vendor-a"));

        mockMvc.perform(post("/auth/vendor/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(20L))
                .andExpect(jsonPath("$.vendorName").value("vendor-a"));
    }

    @Test
    void logoutClearsCookieAndReturnsMessage() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully Logout"));

        verify(authService).deleteCookies(any(HttpServletResponse.class));
    }

    @Test
    void profileReturnsUsernameResponseFromHeaderUsername() throws Exception {
        UsernameResponse response = new UsernameResponse("alice", "9999999999", "alice@example.com");
        when(userService.usernameResponse("alice")).thenReturn(response);

        mockMvc.perform(get("/auth/profile")
                        .header("X-USER-ID", "11")
                        .header("X-USERNAME", "alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.phoneNo").value("9999999999"))
                .andExpect(jsonPath("$.emailId").value("alice@example.com"));

        verify(userService).usernameResponse("alice");
    }
}
