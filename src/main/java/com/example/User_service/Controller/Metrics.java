package com.example.User_service.Controller;

import com.example.User_service.Service.UserMetrics;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Metrics {
    private final UserMetrics userMetrics;

    public Metrics(final UserMetrics metricsService) {
        this.userMetrics = metricsService;
    }

    @GetMapping("/register")
    public String register() {
        userMetrics.incrementUserRegistration();
        return "User Registered";
    }
}
