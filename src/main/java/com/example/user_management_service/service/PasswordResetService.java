package com.example.user_management_service.service;

import com.example.user_management_service.auth.ErrorMessage;
import com.example.user_management_service.model.User;
import com.example.user_management_service.model.dto.ForgotPasswordRequest;
import com.example.user_management_service.model.dto.ResetPasswordRequest;
import com.example.user_management_service.repository.UserRepository;
import com.example.user_management_service.exception.ValidationException;
import com.example.user_management_service.utils.ValidationUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private final RegistrationService registrationService;
    public boolean sendResetToken(ForgotPasswordRequest request) {
        Optional<User> userOptional = userRepository.findByNumber(request.getPhoneNumber()); // Adjust for phone if needed
        if (userOptional.isEmpty()) {
            return false;
        }

        User user = userOptional.get();
        int randomNumber = ThreadLocalRandom.current().nextInt(100000, 1000000);

        user.setResetToken(randomNumber);
        userRepository.save(user);

        registrationService.sendSMS(request.getPhoneNumber(), randomNumber);

        return true;
    }

    public boolean resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByNumber(request.getPhoneNumber()).get();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        userRepository.save(user);
        return true;
    }
}
