package com.example.user_management_service.auth;

import com.example.user_management_service.role.Role;
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
 *
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

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authService.refreshToken(request, response);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(String.valueOf(authService.authenticate(request)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("INCORRECT_NUMBER_OR_PASSWORD");
        }
    }

    @GetMapping("/is-number-exist")
    public ResponseEntity<Boolean> isNumberExist(@RequestParam("number") String number) {
        boolean exists = authService.isUserWithNumberExists(number);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/verification-number")
    public ResponseEntity<Boolean> getVerificationNumber(@RequestParam("email") String email) {
        boolean isCreated = authService.getNewVerificationNumber(email);
        return ResponseEntity.ok(isCreated);
    }

    /**
     * Validates if the current user has the privilege to assign the target role.
     *
     * @param targetRole the role to be assigned
     */
    private void validateRoleAssignment(Role targetRole) {
        Role currentUserRole = roleService.getCurrentUserRole();
        if (!authService.canCreateRole(currentUserRole, targetRole)) {
            throw new UnauthorizedAccessException("You cannot assign the '" + targetRole.name() + "' role.");
        }
    }

    /**
     * Handles the user registration process.
     *
     * @param request the registration request
     * @param roleName the role being registered
     * @return ResponseEntity with status and body
     */
    private ResponseEntity<String> registerUser(RegisterRequest request, String roleName) {
        boolean isRegistered = authService.register(request);
        HttpStatus status = isRegistered ? HttpStatus.ACCEPTED : HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(roleName + " registration " + (isRegistered ? "successful" : "failed"));
    }
}
