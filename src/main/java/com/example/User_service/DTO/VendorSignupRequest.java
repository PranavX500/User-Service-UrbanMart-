package com.example.User_service.DTO;

import com.example.User_service.Model.Role;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
