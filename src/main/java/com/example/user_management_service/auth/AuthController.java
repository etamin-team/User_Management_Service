package com.example.user_management_service.auth;

import com.example.user_management_service.exception.UnauthorizedAccessException;
//import com.example.user_management_service.model.City;
//import com.example.user_management_service.model.Region;
import com.example.user_management_service.model.City;
import com.example.user_management_service.model.Region;
import com.example.user_management_service.model.WorkPlace;
import com.example.user_management_service.model.dto.*;
import com.example.user_management_service.role.AuthRandomNumberResponse;
import com.example.user_management_service.role.Role;
import com.example.user_management_service.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

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
    private final PasswordResetService passwordResetService;
    private final CityRegionService cityRegionService;
    private final UserService userService;

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
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(authService.authenticate(request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/signup-doctor")
    public ResponseEntity<String> signUpDoctor(@RequestBody DoctorSignUpRequest request) {
        AuthRandomNumberResponse response = authService.signUpDoctorWithOutConfirmation(request);
        HttpStatus status = response.equals(AuthRandomNumberResponse.SUCCESS)
                ? HttpStatus.ACCEPTED
                : HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(response.name());
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

    @GetMapping("/workplaces")
    public  ResponseEntity<List<WorkPlace>> getAllWorkPlaces(){
      List<WorkPlace>  workPlaceList= authService.getAllWorkPlaces();
        return ResponseEntity.ok(workPlaceList);
    }

    @GetMapping("/regions")
    public  ResponseEntity<List<Region>> getAllRegions(){
        List<Region>  regionList= cityRegionService.getRegions();
        return ResponseEntity.ok(regionList);
    }

    @GetMapping("/citis")
    public  ResponseEntity<List<City>> getAllCitiesByRegionName(@RequestParam("regionId") Long regionId){
        List<City>  cityList=cityRegionService.getCitiesByRegionId(regionId);
        return ResponseEntity.ok(cityList);
    }

//    @GetMapping("/workplaces")
//    public ResponseEntity<List<WorkPlace>> getAllWorkplaces() {
//        List<WorkPlace> workplaces = userService.getAllWorkplaces();
//        return workplaces.isEmpty()
//                ? ResponseEntity.noContent().build()
//                : ResponseEntity.ok(workplaces);
//    }

}
