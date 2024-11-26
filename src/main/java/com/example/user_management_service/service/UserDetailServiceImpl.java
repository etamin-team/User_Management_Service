package com.example.user_management_service.service;


import com.example.user_management_service.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Date-11/19/2024
 * By Sardor Tokhirov
 * Time-5:08 PM (GMT+5)
 */

@Service
@AllArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
    private final static String USER_NOT_FOUND="user with %s userId  not found";
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        return (UserDetails) userRepository.findById(UUID.fromString(userId))
                .orElseThrow(()->new UsernameNotFoundException(String.format(USER_NOT_FOUND,userId)));
    }
}
