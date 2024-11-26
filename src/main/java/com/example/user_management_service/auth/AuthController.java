package com.example.user_management_service.auth;

import com.example.user_management_service.exception.UnauthorizedAccessException;
import com.example.user_management_service.model.dto.*;
import com.example.user_management_service.role.AuthRandomNumberResponse;
import com.example.user_management_service.role.Role;
import com.example.user_management_service.service.PasswordResetService;
import com.example.user_management_service.service.RegistrationService;
import com.example.user_management_service.service.RoleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * AuthController
 * Handles authentication and registration requests.
 * <p>
 * Date: 11/20/2024
 * Author: Sardor Tokhirov
 * Time: 4:05 PM (GMT+5)
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final RegistrationService authService;
    private final RoleService roleService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            boolean success = passwordResetService.sendResetToken(request);
            if (success) {
                return ResponseEntity.status(HttpStatus.ACCEPTED).body("Password reset link sent");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending password reset link");
        }
    }

    // Reset password endpoint (step 2: user submits the token and new password)
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            boolean success = passwordResetService.resetPassword(request);
            if (success) {
                return ResponseEntity.status(HttpStatus.ACCEPTED).body("Password reset successfully");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid reset token");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error resetting password");
        }
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(String.valueOf(authService.authenticate(request)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("INCORRECT_NUMBER_OR_PASSWORD");
        }
    }

    @PostMapping("/signup-doctor")
    public ResponseEntity<String> signUpDoctor(@RequestBody DoctorSignUpRequest request) {
        AuthRandomNumberResponse response = authService.signUpDoctor(request);
        HttpStatus status = response.equals(AuthRandomNumberResponse.SUCCESS)
                ? HttpStatus.ACCEPTED
                : HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(response.name());
    }

    @PostMapping("/register-doctor")
    public ResponseEntity<Void> registerDoctor(@RequestBody RegisterRequest request) {
        boolean isRegistered = authService.register(request);
        return ResponseEntity.status(isRegistered ? HttpStatus.ACCEPTED : HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/register-admin")
    public ResponseEntity<String> registerAdmin(@RequestBody RegisterRequest request) {
        validateRoleAssignment(Role.ADMIN);
        return registerUser(request, "Admin");
    }

    @PostMapping("/register-manager")
    public ResponseEntity<String> registerManager(@RequestBody RegisterRequest request) {
        validateRoleAssignment(Role.MANAGER);
        return registerUser(request, "Manager");
    }

    @PostMapping("/register-superadmin")
    public ResponseEntity<String> registerSuperAdmin(@RequestBody RegisterRequest request) {
        validateRoleAssignment(Role.SUPERADMIN);
        return registerUser(request, "SuperAdmin");
    }

    @GetMapping("/is-number-exist")
    public ResponseEntity<Boolean> isNumberExists(@RequestParam("number") String number) {
        boolean exists = authService.isUserWithNumberExists(number);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/is-email-exist")
    public ResponseEntity<Boolean> isEmailExists(@RequestParam("email") String email) {
        boolean exists = authService.isUserWithEmailExists(email);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/verification-number-phone-number")
    public ResponseEntity<Boolean> getVerificationNumberForPhoneNumber(@RequestParam("number") String number) {
        boolean isCreated = authService.getNewVerificationNumber(number, false);
        return ResponseEntity.ok(isCreated);
    }

    @GetMapping("/verification-number-email")
    public ResponseEntity<Boolean> getVerificationNumber(@RequestParam("email") String email) {
        boolean isCreated = authService.getNewVerificationNumber(email, true);
        return ResponseEntity.ok(isCreated);
    }

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authService.refreshToken(request, response);
    }

    private void validateRoleAssignment(Role targetRole) {
        Role currentUserRole = roleService.getCurrentUserRole();
        if (!authService.canCreateRole(currentUserRole, targetRole)) {
            throw new UnauthorizedAccessException("You cannot assign the '" + targetRole.name() + "' role.");
        }
    }


    private ResponseEntity<String> registerUser(RegisterRequest request, String roleName) {
        boolean isRegistered = authService.register(request);
        HttpStatus status = isRegistered ? HttpStatus.ACCEPTED : HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(roleName + " registration " + (isRegistered ? "successful" : "failed"));
    }



}
