package com.example.User_service.Service;

import com.example.User_service.DTO.OtpRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EmailProducer {

    private final KafkaTemplate<String, OtpRequest> kafkaTemplate;

    public EmailProducer(KafkaTemplate<String, OtpRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEmailId(OtpRequest otpRequest){
        System.out.println("Sending to Kafka: " + otpRequest);
        kafkaTemplate.send("Email-topic", otpRequest);
    }
}

