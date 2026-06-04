package com.example.User_service.Service;

import com.example.User_service.DTO.FlagResponse;
import com.example.User_service.Model.User;
import com.example.User_service.Model.Vendor;
import com.example.User_service.Repositery.UserRepositery;
import com.example.User_service.Repositery.VendorRepositery;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailConsumer {
    private final UserRepositery userRepository;
    private final VendorRepositery vendorRepositery;

    @KafkaListener(
            topics = "Otp-Success-topic",
            groupId = "User-group",
            properties = {
                    "bootstrap.servers=${spring.kafka.bootstrap-servers}",
                    "key.deserializer=org.apache.kafka.common.serialization."
                            + "StringDeserializer",
                    "value.deserializer=org.springframework.kafka.support."
                            + "serializer.JsonDeserializer",
                    "spring.json.value.default.type=com.example.User_service."
                            + "DTO.FlagResponse",
                    "spring.json.trusted.packages=*"
            })
    public void isVerified(final FlagResponse flagResponse) {
        if (flagResponse.isVerified()) {
            final User user = userRepository.findByEmailId(
                    flagResponse.getEmailId());

            if (user != null) {
                user.setVerify(true);
                userRepository.save(user);
            }
        }

        if (flagResponse.isVerified()) {
            final Vendor vendor = vendorRepositery.findByEmailId(
                    flagResponse.getEmailId());

            if (vendor != null) {
                vendor.setVerify(true);
                vendorRepositery.save(vendor);
            }
        }
    }
}
