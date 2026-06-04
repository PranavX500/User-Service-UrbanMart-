package com.example.User_service.Repositery;

import com.example.User_service.Model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VendorRepositery extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findByVendorName(String vendorName);

    Vendor findByEmailId(String emailId);
}
