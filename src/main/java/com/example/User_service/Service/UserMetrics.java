package com.example.User_service.Service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class UserMetrics {
    private final Counter userRegistrationCounter;

    public UserMetrics(final MeterRegistry registry) {
        this.userRegistrationCounter =
                Counter.builder("user_registration_total")
                        .description("Total users registered")
                        .register(registry);
    }

    public void incrementUserRegistration() {
        userRegistrationCounter.increment();
    }
}
