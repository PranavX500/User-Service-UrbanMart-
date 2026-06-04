package com.example.User_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendorSignupRequest {
    private String vendorName;
    private String password;
    private String emailId;
    private String phoneNo;
}
