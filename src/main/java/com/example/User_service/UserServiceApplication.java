package com.example.User_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin
public final class UserServiceApplication {
    private UserServiceApplication() {
    }

    public static void main(final String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
