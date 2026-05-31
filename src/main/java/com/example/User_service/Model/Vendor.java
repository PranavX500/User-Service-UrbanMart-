package com.example.User_service.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.web.WebProperties;

@Entity
@Table(name="Vendor_INFO")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Vendor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long Id;
    @Column(nullable = false)
    private String vendorName;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String emailId;
    @Column(nullable = false)
    private String phoneNo;
    private boolean verify;
    @Enumerated(EnumType.STRING)
    private Role role;

}
