package com.example.user_management_service.config;

import com.example.user_management_service.service.UserDetailServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * Date-11/19/2024
 * By Sardor Tokhirov
 * Time-4:08 PM (GMT+5)
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    private final UserDetailServiceImpl userDetailsService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(Collections.singletonList(new FormHttpMessageConverter()));
    }

}
