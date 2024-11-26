package com.example.user_management_service.service;

import com.example.user_management_service.model.User;
import com.example.user_management_service.model.dto.ForgotPasswordRequest;
import com.example.user_management_service.model.dto.ResetPasswordRequest;
import com.example.user_management_service.repository.UserRepository;
import com.example.user_management_service.exception.ValidationException;
import com.example.user_management_service.utils.ValidationUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
//    private final JavaMailSender mailSender;

//    @Value("${app.url}")
//    private String appUrl;

    // Send reset token to the user (email or phone)
    public boolean sendResetToken(ForgotPasswordRequest request) {
//        Optional<User> userOptional = userRepository.findByEmail(request.getEmailOrPhone()); // Adjust for phone if needed
//        if (userOptional.isEmpty()) {
//            return false;
//        }

//        User user = userOptional.get();
//        String resetToken = UUID.randomUUID().toString();  // Generate a unique reset token
//
//        // Save the reset token to the user record (you can store it in a separate table if needed)
//        user.setResetToken(resetToken);
//        userRepository.save(user);
//
//        // Send email with the reset link
//        sendResetEmail(user.getEmail(), resetToken);
//
        return true;
    }

    // Send password reset link via email
    private void sendResetEmail(String email, String resetToken) {
//        String resetUrl = appUrl + "/reset-password?token=" + resetToken;
//
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(email);
//        message.setSubject("Password Reset Request");
//        message.setText("To reset your password, click the link below:\n" + resetUrl);
//        mailSender.send(message);
    }

    // Reset the user's password using the token
    public boolean resetPassword(ResetPasswordRequest request) {
//        Optional<User> userOptional = userRepository.findByResetToken(request.getResetToken());
//        if (userOptional.isEmpty()) {
//            throw new ValidationException("Invalid reset token");
//        }
//
//        User user = userOptional.get();
//        user.setPassword(request.getNewPassword());
//        user.setResetToken(null); // Invalidate the token after use
//        userRepository.save(user);
//
        return true;
    }
}
