package com.example.user_management_service.auth;

import com.example.user_management_service.exception.DataNotFoundException;
import com.example.user_management_service.exception.InvalidTokenException;
import com.example.user_management_service.exception.UserAlreadyExistsException;
import com.example.user_management_service.model.District;
import com.example.user_management_service.model.Region;
import com.example.user_management_service.model.WorkPlace;
import com.example.user_management_service.model.dto.*;
import com.example.user_management_service.role.AuthRandomNumberResponse;
        import com.example.user_management_service.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
    private final DistrictRegionService districtRegionService;

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            boolean success = passwordResetService.sendResetToken(request);
            if (success) {
                return ResponseEntity.status(HttpStatus.ACCEPTED).body("Password reset link sent successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid phone number format.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred while sending the password reset link.");
        }
    }


    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.authenticate(request);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found."));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Incorrect password."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }


    @PostMapping("/signup-doctor")
    public ResponseEntity<Object> signUpDoctor(@RequestBody DoctorSignUpRequest request) {
        try {
            AuthRandomNumberResponse response = authService.signUpDoctorWithOutConfirmation(request);

            if (response == AuthRandomNumberResponse.SUCCESS) {
                return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Doctor registered successfully."));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Doctor registration failed."));
            }
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "User already exists."));
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
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
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            AuthResponse authResponse = authService.refreshToken(request, response);
            return ResponseEntity.ok(authResponse);
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred."));
        }
    }


    @GetMapping("/workplaces")
    public  ResponseEntity<List<WorkPlaceDTO>> getAllWorkPlaces(@RequestParam("regionId") Long regionId,@RequestParam("districtId") Long districtId ){
      List<WorkPlaceDTO>  workPlaceList= authService.getAllWorkPlaces(regionId,districtId);
        return ResponseEntity.ok(workPlaceList);
    }

    @GetMapping("/regions")
    public  ResponseEntity<List<RegionDTO>> getAllRegions(){
        List<RegionDTO>  regionList= districtRegionService.getRegions();
        return ResponseEntity.ok(regionList);
    }

    @GetMapping("/districts")
    public ResponseEntity<List<DistrictDTO>> getAllDistrictsByRegionId(@RequestParam("regionId") Long regionId) {
        List<DistrictDTO> districtList = districtRegionService.getDistrictsByRegionId(regionId);
        if (districtList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(districtList);
    }

    @GetMapping("/region")
    public ResponseEntity<RegionDTO> getRegionById(@RequestParam("regionId") Long regionId) {
        return ResponseEntity.ok(districtRegionService.getRegionById(regionId));
    }

    @GetMapping("/district")
    public ResponseEntity<DistrictDTO> getAllDistrictById(@RequestParam("districtId") Long districtId) {
        return ResponseEntity.ok(districtRegionService.getDistrictById(districtId));
    }

//    @GetMapping("/workplaces")
//    public ResponseEntity<List<WorkPlace>> getAllWorkplaces() {
//        List<WorkPlace> workplaces = userService.getAllWorkplaces();
//        return workplaces.isEmpty()
//                ? ResponseEntity.noContent().build()
//                : ResponseEntity.ok(workplaces);
//    }

}
