package com.example.User_service.Service;

import com.example.User_service.Repositery.UserRepositery;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoadUsername implements UserDetailsService {
    private final UserRepositery repo;

    @Override
    public UserDetails loadUserByUsername(final String username)
            throws UsernameNotFoundException {
        return repo.findByUsername(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException("User not found"));
    }
}
