package com.example.User_service.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Vendor_INFO")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Vendor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
