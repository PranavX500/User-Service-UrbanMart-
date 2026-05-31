package com.example.User_service.Service;

import com.example.User_service.Model.User;
import com.example.User_service.Model.Vendor;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class AuthUtil {
    @Value("${jwt.secretKey}")
    private String jwtSecretKey;
    public SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessTokenforvendor(Vendor vendor){
        return Jwts.builder()
                .subject(vendor.getVendorName())
                .claim("role", vendor.getRole())
                .claim("email", vendor.getEmailId())
                .claim("userId",vendor.getId())
                .issuedAt(new Date())

                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    public String generateAccessTokenforuser(User user){
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("role", user.getRole())
                .claim("email", user.getEmailId())
                .claim("userId",user.getId())
                .issuedAt(new Date())

                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }


//    public String getUsernameFromToken(String token) {
//        return Jwts.parser()
//                .verifyWith(getSecretKey())
//                .build()
//                .parseSignedClaims(token)
//                .getPayload()
//                .getSubject();
//    }

}
