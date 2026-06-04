package com.example.User_service.Service;

import com.example.User_service.DTO.OtpRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EmailProducer {
    private final KafkaTemplate<String, OtpRequest> kafkaTemplate;

    public EmailProducer(
            final KafkaTemplate<String, OtpRequest> producerTemplate) {
        this.kafkaTemplate = producerTemplate;
    }

    public void sendEmailId(final OtpRequest otpRequest) {
        kafkaTemplate.send("Email-topic", otpRequest);
    }
}
