package com.example.user_management_service.controller;

import com.example.user_management_service.model.User;
import com.example.user_management_service.model.dto.ChangePasswordRequest;
import com.example.user_management_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Date-11/27/2024
 * By Sardor Tokhirov
 * Time-2:55 AM (GMT+5)
 */
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") String userId) {
        User user = userService.getUserById(userId);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }
    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        boolean isPasswordChanged = userService.changePassword(changePasswordRequest);

        if (isPasswordChanged) {
            return ResponseEntity.ok("Password successfully updated.");
        } else {
            return ResponseEntity.badRequest().body("Password change failed. Please check your old password.");
        }
    }

}
