package com.example.user_management_service.controller;

import com.example.user_management_service.exception.UnauthorizedAccessException;
import com.example.user_management_service.model.FieldForceRegions;
import com.example.user_management_service.model.User;
import com.example.user_management_service.model.dto.*;
import com.example.user_management_service.role.Role;
import com.example.user_management_service.role.UserStatus;
import com.example.user_management_service.service.RegistrationService;
import com.example.user_management_service.service.RoleService;
import com.example.user_management_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

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

    @GetMapping("/password-compare")
    public ResponseEntity<Boolean> comparePassword(@RequestParam(required = true) UUID userId, @RequestParam(required = true) String password) {
        boolean isDeleted = userService.comparePassword(userId, password);
        return ResponseEntity.ok(isDeleted);

    }

    @PostMapping("/register-doctor")
    public ResponseEntity<UserDTO> registerDoctor(@RequestBody RegisterRequest request) {
        UserDTO isRegistered = authService.register(request, Role.DOCTOR, UserStatus.ENABLED, roleService.getCurrentUserId());
        return ResponseEntity.ok(isRegistered);
    }

    @PostMapping("/register-admin")
    public ResponseEntity<String> registerAdmin(@RequestBody RegisterRequest request) {
        validateRoleAssignment(Role.ADMIN);
        return registerUser(request, "Admin", Role.ADMIN);
    }

    @PostMapping("/register-fieldforce")
    public ResponseEntity<String> registerFieldForce(@RequestBody RegisterRequest request, @RequestParam List<Long> regionIds) {
        validateRoleAssignment(Role.FIELDFORCE);
        return registerUserFieldForce(request, "Fieldforce", Role.FIELDFORCE,regionIds);
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

    @PostMapping("/register-medagent")
    public ResponseEntity<String> registerMedAgent(@RequestBody RegisterRequest request) {
        validateRoleAssignment(Role.MEDAGENT);
        return registerUser(request, "MedAgent", Role.MEDAGENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") UUID userId) {
        UserDTO user = userService.getUserById(userId);
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
    public ResponseEntity<List<UserDTO>> getDoctors(
            @RequestParam(required = false) UUID creatorId,
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Long districtId,
            @RequestParam(required = false) Long workplaceId,
            @RequestParam(required = false) String nameQuery
    ) {
        List<UserDTO> doctors = userService.getDoctors(creatorId, regionId, districtId, workplaceId, nameQuery);
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/doctors-page")
    public ResponseEntity<Page<UserDTO>> getDoctorsPage(
            @RequestParam(required = false) UUID creatorId,
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Long districtId,
            @RequestParam(required = false) Long workplaceId,
            @RequestParam(required = false) String nameQuery,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<UserDTO> doctors = userService.getDoctorsPage(creatorId, regionId, districtId, workplaceId, nameQuery, page, size);
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/doctor-data")
    public ResponseEntity<DoctorsInfoDTO> getDoctorData(
            @RequestParam(required = false) UUID creatorId,
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Long districtId,
            @RequestParam(required = false) Long workplaceId,
            @RequestParam(required = false) String nameQuery
    ) {
        DoctorsInfoDTO info = userService.getDoctorsInfo(creatorId, regionId, districtId, workplaceId, nameQuery);
        return ResponseEntity.ok(info);
    }
    @GetMapping("/managers")
    public ResponseEntity<List<UserDTO>> getManagers(
            @RequestParam(required = false) UUID creatorId,
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Long districtId,
            @RequestParam(required = false) Long workplaceId,
            @RequestParam(required = false) String nameQuery) {
        List<UserDTO> users = userService.getManagers(creatorId, regionId, districtId, workplaceId, nameQuery);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/superadmins")
    public ResponseEntity<List<UserDTO>> getSuperAdmins(
            @RequestParam(required = false) UUID creatorId,
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Long districtId,
            @RequestParam(required = false) Long workplaceId,
            @RequestParam(required = false) String nameQuery) {
        List<UserDTO> users = userService.getSuperAdmins(creatorId, regionId, districtId, workplaceId, nameQuery);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/admins")
    public ResponseEntity<List<UserDTO>> getAdmins(
            @RequestParam(required = false) UUID creatorId,
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Long districtId,
            @RequestParam(required = false) Long workplaceId,
            @RequestParam(required = false) String nameQuery) {
        List<UserDTO> users = userService.getAdmins(creatorId, regionId, districtId, workplaceId, nameQuery);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/medagents")
    public ResponseEntity<List<UserDTO>> getMedAgents(
            @RequestParam(required = false) UUID creatorId,
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Long districtId,
            @RequestParam(required = false) Long workplaceId,
            @RequestParam(required = false) String nameQuery) {
        List<UserDTO> users = userService.getMedAgents(creatorId, regionId, districtId, workplaceId, nameQuery);
        return ResponseEntity.ok(users);
    }

//    @PostMapping("/upload-doctors")
//    public ResponseEntity<String> uploadDoctors(@RequestBody List<RegisterRequest> requests) {
//        return uploadUsers(requests, Role.DOCTOR, "Doctors");
//    }
    @PostMapping(value = "/upload-doctors", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadDoctors(@RequestParam("file") MultipartFile file) throws IOException {
        List<RegisterRequest> requests = userService.parseFileDoctors(file);
        return uploadUsers(requests, Role.DOCTOR, "Doctors");
    }

    @PostMapping("/upload-admins")
    public ResponseEntity<String> uploadAdmins(@RequestBody List<RegisterRequest> requests) {
        validateRoleAssignment(Role.ADMIN);
        return uploadUsers(requests, Role.ADMIN, "Admins");
    }

    @PostMapping("/upload-managers")
    public ResponseEntity<String> uploadManagers(@RequestBody List<RegisterRequest> requests) {
        validateRoleAssignment(Role.MANAGER);
        return uploadUsers(requests, Role.MANAGER, "Managers");
    }

    @PostMapping("/upload-superadmins")
    public ResponseEntity<String> uploadSuperAdmins(@RequestBody List<RegisterRequest> requests) {
        validateRoleAssignment(Role.SUPERADMIN);
        return uploadUsers(requests, Role.SUPERADMIN, "SuperAdmins");
    }

    @PostMapping("/upload-medagents")
    public ResponseEntity<String> uploadMedAgents(@RequestBody List<RegisterRequest> requests) {
        validateRoleAssignment(Role.MEDAGENT);
        return uploadUsers(requests, Role.MEDAGENT, "MedAgents");
    }

    private ResponseEntity<String> uploadUsers(List<RegisterRequest> requests, Role role, String roleName) {
        boolean allRegistered = authService.registerAll(requests, role, UserStatus.ENABLED, roleService.getCurrentUserId());
        String responseMessage = allRegistered
                ? roleName + " upload successful"
                : roleName + " upload partially failed. Please check the input data.";
        return ResponseEntity.status(allRegistered ? HttpStatus.OK : HttpStatus.PARTIAL_CONTENT).body(responseMessage);
    }


    private void validateRoleAssignment(Role targetRole) {
        Role currentUserRole = roleService.getCurrentUserRole();
        if (!authService.canCreateRole(currentUserRole, targetRole)) {
            throw new UnauthorizedAccessException("You cannot assign the '" + targetRole.name() + "' role.");
        }
    }


    private ResponseEntity<String> registerUser(RegisterRequest request, String roleName, Role role) {
        boolean isRegistered = authService.register(request, role, UserStatus.ENABLED, roleService.getCurrentUserId()) != null;
        HttpStatus status = isRegistered ? HttpStatus.ACCEPTED : HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(roleName + " registration " + (isRegistered ? "successful" : "failed"));
    }
    private ResponseEntity<String> registerUserFieldForce(RegisterRequest request, String roleName, Role role,List<Long> regionIds) {
        UUID uuid=authService.register(request, role, UserStatus.ENABLED, roleService.getCurrentUserId()).getUserId();
        FieldForceRegionsDTO fieldForceRegionsDTO = new FieldForceRegionsDTO();
        fieldForceRegionsDTO.setFieldForceId(uuid);
        fieldForceRegionsDTO.setForceRegionIds(regionIds);
        userService.createOrUpdateFieldForceRegions(fieldForceRegionsDTO);
        HttpStatus status = HttpStatus.ACCEPTED;
        return ResponseEntity.status(status).body(roleName + " registration " + "successful" );
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable("id") String userId,
            @RequestBody UserDTO updatedUser
    ) {
        UserDTO user = userService.updateUser(userId, updatedUser);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") UUID userId) {
        boolean isDeleted = userService.deleteUser(userId);
        return isDeleted
                ? ResponseEntity.ok("User successfully deleted.")
                : ResponseEntity.notFound().build();
    }


    @PostMapping("/field-force-regions/update")
    public ResponseEntity<Void> createOrUpdateFieldForceRegions(@RequestBody FieldForceRegionsDTO fieldForceRegions) {
        userService.createOrUpdateFieldForceRegions(fieldForceRegions);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/field-force-regions/user/{userId}")
    public ResponseEntity<FieldForceRegionsInfoDTO> getFieldForceRegionByUserId(@PathVariable UUID userId) {
        FieldForceRegionsInfoDTO dto = userService.getFieldForceRegionsByUserId(userId);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/field-forces")
    public ResponseEntity<List<FieldForceRegionsInfoDTO>> getFieldForceRegions() {
        List<FieldForceRegionsInfoDTO> dto = userService.getFieldForceRegions();
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }
}
