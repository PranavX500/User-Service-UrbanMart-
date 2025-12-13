package com.example.User_service.DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsernameResponse {
    private String username;

    private String phoneNo;
    private String emailId;

    public String setPhoneNo() {
        return phoneNo;
    }
}
