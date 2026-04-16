package com.example.User_service.Service;

import com.example.User_service.DTO.UsernameResponse;
import com.example.User_service.Model.Role;
import com.example.User_service.Model.User;
import com.example.User_service.Repositery.UserRepositery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepositery userRepositery;

    @InjectMocks
    private UserService userService;

    @Test
    void usernameResponseMapsUserToDto() {
        User user = User.builder()
                .username("alice")
                .emailId("alice@example.com")
                .phoneNo("9999999999")
                .role(Role.ROLE_USER)
                .build();
        when(userRepositery.findByUsername("alice")).thenReturn(Optional.of(user));

        UsernameResponse result = userService.usernameResponse("alice");

        assertThat(result.getUsername()).isEqualTo("alice");
        assertThat(result.getEmailId()).isEqualTo("alice@example.com");
        assertThat(result.getPhoneNo()).isEqualTo("9999999999");
    }

    @Test
    void usernameResponseThrowsWhenUserDoesNotExist() {
        when(userRepositery.findByUsername("missing")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.usernameResponse("missing"));

        assertThat(exception.getMessage()).isEqualTo("User not found");
    }
}
