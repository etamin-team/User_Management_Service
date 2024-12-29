package com.example.user_management_service.controller;

import com.example.user_management_service.exception.UnauthorizedAccessException;
import com.example.user_management_service.model.User;
import com.example.user_management_service.model.dto.ChangePasswordRequest;
import com.example.user_management_service.model.dto.RegisterRequest;
import com.example.user_management_service.role.Role;
import com.example.user_management_service.role.UserStatus;
import com.example.user_management_service.service.RegistrationService;
import com.example.user_management_service.service.RoleService;
import com.example.user_management_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Date-11/27/2024
 * By Sardor Tokhirov
 * Time-2:55 AM (GMT+5)
 */
@RestController
@RequestMapping("/api/v1/user")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final RegistrationService authService;
    private final RoleService roleService;

    @Autowired
    public UserController(UserService userService, RegistrationService authService, RoleService roleService) {
        this.userService = userService;
        this.authService = authService;
        this.roleService = roleService;
    }

    @PostMapping("/register-doctor")
    public ResponseEntity<Void> registerDoctor(@RequestBody RegisterRequest request) {
        boolean isRegistered = authService.register(request, Role.DOCTOR, UserStatus.ENABLED);
        return ResponseEntity.status(isRegistered ? HttpStatus.ACCEPTED : HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/register-admin")
    public ResponseEntity<String> registerAdmin(@RequestBody RegisterRequest request) {
        validateRoleAssignment(Role.ADMIN);
        return registerUser(request, "Admin", Role.ADMIN);
    }

    @PostMapping("/register-manager")
    public ResponseEntity<String> registerManager(@RequestBody RegisterRequest request) {
        validateRoleAssignment(Role.MANAGER);
        return registerUser(request, "Manager", Role.MANAGER);
    }

    @PostMapping("/register-superadmin")
    public ResponseEntity<String> registerSuperAdmin(@RequestBody RegisterRequest request) {
        validateRoleAssignment(Role.SUPERADMIN);
        return registerUser(request, "SuperAdmin", Role.SUPERADMIN);
    }

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

    @GetMapping("/doctors")
    public ResponseEntity<List<User>> getDoctors(
            @RequestParam(required = false) String creatorId,
            @RequestParam(required = false) Long countryId,
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Long workplaceId,
            @RequestParam(required = false) String nameQuery
    ) {
        List<User> doctors = userService.getDoctors(creatorId, countryId, regionId, workplaceId, nameQuery);
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/managers")
    public ResponseEntity<List<User>> getManagers(
            @RequestParam(required = false) String creatorId,
            @RequestParam(required = false) Long countryId,
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Long workplaceId,
            @RequestParam(required = false) String query) {

        List<User> users = userService.getManagers(creatorId, countryId, regionId, workplaceId, query);
        return users.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(users);
    }

    private void validateRoleAssignment(Role targetRole) {
        Role currentUserRole = roleService.getCurrentUserRole();
        if (!authService.canCreateRole(currentUserRole, targetRole)) {
            throw new UnauthorizedAccessException("You cannot assign the '" + targetRole.name() + "' role.");
        }
    }


    private ResponseEntity<String> registerUser(RegisterRequest request, String roleName, Role role) {
        boolean isRegistered = authService.register(request, role, UserStatus.ENABLED);
        HttpStatus status = isRegistered ? HttpStatus.ACCEPTED : HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(roleName + " registration " + (isRegistered ? "successful" : "failed"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable("id") String userId,
            @RequestBody User updatedUser
    ) {
        User user = userService.updateUser(userId, updatedUser);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") String userId) {
        boolean isDeleted = userService.deleteUser(userId);
        return isDeleted
                ? ResponseEntity.ok("User successfully deleted.")
                : ResponseEntity.notFound().build();
    }

}
