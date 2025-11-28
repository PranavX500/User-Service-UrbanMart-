package com.example.User_service.Repositery;

import com.example.User_service.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepositery extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);
    User findByEmailId(String emailId);

}
