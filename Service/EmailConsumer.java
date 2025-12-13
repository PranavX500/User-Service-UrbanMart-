package com.example.User_service.Service;


import com.example.User_service.DTO.FlagResponse;
import com.example.User_service.DTO.SignupResponse;
import com.example.User_service.Model.User;
import com.example.User_service.Repositery.UserRepositery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmailConsumer {

    @Autowired
    private UserRepositery userRepository;

    @KafkaListener(topics = "Otp-Success-topic", groupId = "User-group",properties = {
            "bootstrap.servers=${spring.kafka.bootstrap-servers}",
            "key.deserializer=org.apache.kafka.common.serialization.StringDeserializer",
            "value.deserializer=org.springframework.kafka.support.serializer.JsonDeserializer",
            "spring.json.value.default.type=com.example.User_service.DTO.FlagResponse",
            "spring.json.trusted.packages=*"
    })
    public void isVerified(FlagResponse flagResponse) {

        System.out.println("Received OTP Response: " + flagResponse);

        if (flagResponse.isVerified()) {
            User user = userRepository.findByEmailId(flagResponse.getEmailId());
            if (user != null) {
                user.setVerify(true);
                userRepository.save(user);
                System.out.println("User saved successfully after OTP verification.");
            } else {
                System.out.println("User not found for email: " + flagResponse.getEmailId());
            }
        }
    }
}
