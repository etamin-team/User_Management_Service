package com.example.user_management_service.service;


import com.example.user_management_service.auth.*;
import com.example.user_management_service.config.JwtService;
import com.example.user_management_service.message.sms.SmsService;
import com.example.user_management_service.model.*;
import com.example.user_management_service.model.dto.AuthRequest;
import com.example.user_management_service.model.dto.DoctorSignUpRequest;
import com.example.user_management_service.model.dto.RegisterRequest;
import com.example.user_management_service.model.dto.WorkPlaceDTO;
import com.example.user_management_service.repository.*;
import com.example.user_management_service.role.AuthRandomNumberResponse;
import com.example.user_management_service.role.Role;
import com.example.user_management_service.role.UserStatus;
import com.example.user_management_service.token.Token;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.example.user_management_service.token.TokenType.BEARER;

/**
 * Date-11/20/2024
 * By Sardor Tokhirov
 * Time-4:49 PM (GMT+5)
 */
@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final DistrictRegionService districtRegionService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final WorkPlaceRepository workPlaceRepository;
    private final SmsService smsService;

    private final VerificationNumberRepository verificationNumberRepository;

    public List<WorkPlaceDTO> getAllWorkPlaces() {
        return convertToDTOs(workPlaceRepository.findAll());
    }

    public List<WorkPlaceDTO> convertToDTOs(List<WorkPlace> workPlaces) {
        return workPlaces.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private WorkPlaceDTO convertToDTO(WorkPlace workPlace) {
        return new WorkPlaceDTO(
                workPlace.getId(),
                workPlace.getName(),
                workPlace.getAddress(),
                workPlace.getDescription(),
                workPlace.getPhone(),
                workPlace.getEmail(),
                workPlace.getChiefDoctor() != null ? workPlace.getChiefDoctor().getUserId() : null,
                workPlace.getDistrict() != null ? workPlace.getDistrict().getId() : null
        );

    }

    public AuthRandomNumberResponse signUpDoctorWithOutConfirmation(DoctorSignUpRequest request) {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName(request.getFirstName());
        registerRequest.setLastName(request.getLastName());
        registerRequest.setMiddleName(request.getMiddleName());

        registerRequest.setPassword(request.getPassword());
        registerRequest.setDistrictId(request.getDistrictId());

        registerRequest.setPhoneNumber(request.getPhoneNumber());
        registerRequest.setNumber(request.getNumber());
        registerRequest.setPhonePrefix(request.getPhonePrefix());

        registerRequest.setWorkPlaceId(request.getWorkPlaceId());
        registerRequest.setBirthDate(request.getBirthDate());

        registerRequest.setFieldName(request.getFieldName());
        registerRequest.setPosition(request.getPosition());
        registerRequest.setGender(request.getGender());

        register(registerRequest, Role.DOCTOR, UserStatus.PENDING,null);
        return AuthRandomNumberResponse.SUCCESS;
    }


    public boolean register(RegisterRequest request, Role role, UserStatus userStatus, UUID creatorId) {
        User user = createUserRequest(request, role);
        user.setUserId(UUID.randomUUID());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(userStatus);
        user.setCreatorId(String.valueOf(creatorId));
        userRepository.save(user);
        return true;
    }

    private User createUserRequest(RegisterRequest request, Role role) {
        User newUser = new User();

        if (request.getDistrictId() != null) {
            District district = districtRegionService.getDistrict(request.getDistrictId());
            newUser.setDistrict(district);
        }
        newUser.setFirstName(request.getFirstName());
        newUser.setMiddleName(request.getMiddleName());
        newUser.setLastName(request.getLastName());
        newUser.setPhoneNumber(request.getPhoneNumber());
        newUser.setPhonePrefix(request.getPhonePrefix());
        newUser.setNumber(request.getNumber());
        newUser.setPassword(request.getPassword());
        newUser.setCreatedDate(LocalDateTime.now());
        newUser.setRole(role);
        newUser.setFieldName(request.getFieldName());
        newUser.setPosition(request.getPosition());
        newUser.setGender(request.getGender());
        newUser.setLastUpdateDate(LocalDateTime.now());
        newUser.setDateOfBirth(request.getBirthDate());

        WorkPlace workPlace = workPlaceRepository.findById(request.getWorkPlaceId()).get();
        newUser.setWorkplace(workPlace);
        return newUser;
    }

    public boolean registerAll(List<RegisterRequest> requests, Role role, UserStatus userStatus, UUID creatorId) {
        try {
            List<User> users = requests.stream()
                    .map(request -> createUserFromRequest(request, role, userStatus, creatorId))
                    .toList();
            userRepository.saveAll(users);
            return true;
        } catch (Exception e) {
            // Handle any specific logging or exceptions
            return false;
        }
    }

    private User createUserFromRequest(RegisterRequest request, Role role, UserStatus userStatus, UUID creatorId) {
        User user = createUserRequest(request, role);
        user.setUserId(UUID.randomUUID());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(userStatus);
        user.setCreatorId(String.valueOf(creatorId));
        return user;
    }


    private String getMissingFields(DoctorSignUpRequest request) {
        StringBuilder missingFields = new StringBuilder();

        if (request.getFirstName() == null) missingFields.append("firstName, ");
        if (request.getPhoneNumber() == null) missingFields.append("phoneNumber, ");
        if (request.getPhonePrefix() == null) missingFields.append("phonePrefix, ");
        if (request.getLastName() == null) missingFields.append("lastName, ");
        if (request.getPassword() == null) missingFields.append("password, ");
        if (request.getDistrictId() == null) missingFields.append("District, ");

        if (missingFields.length() > 0) {
            missingFields.setLength(missingFields.length() - 2);
        }

        return missingFields.toString();
    }

    public boolean isUserWithEmailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean isUserWithNumberExists(String number) {
        return userRepository.findByNumber(number).isPresent();
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userId;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userId = jwtService.extractUserId(refreshToken);
        if (userId != null) {
            var userDetails = this.userRepository.findById(UUID.fromString(userId)).orElseThrow();
            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                var accessToken = jwtService.generateToken(userDetails);
                var authResponse = AuthResponse.builder().refreshToken(refreshToken).accsesToken(accessToken).build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }

        }
    }


    public void sendSMS(String toPhoneNumber, int verificationCode) {
        smsService.sendSMS(toPhoneNumber, String.valueOf(verificationCode));
    }

    public boolean getNewVerificationNumber(String number, boolean isEmail) {
        if (verificationNumberRepository.findByNumber(number).isPresent()) {
            VerificationNumber verificationNumber = verificationNumberRepository.findByNumber(number).get();
            if (ChronoUnit.MINUTES.between(LocalDateTime.now(), verificationNumber.getExpirationDate()) > 0 && verificationNumber.getAttempts() > 6)
                return false;
            verificationNumberRepository.deleteByUser(number);
        }
        int randomNumber = ThreadLocalRandom.current().nextInt(100000, 1000000);
        LocalDateTime expirationDate = LocalDateTime.now().plus(15, ChronoUnit.MINUTES);
        System.out.println("randomNumber: " + randomNumber);
        sendSMS(number, randomNumber);


        VerificationNumber verificationNumber = new VerificationNumber(number, randomNumber, expirationDate);
        verificationNumberRepository.save(verificationNumber);
        return true;
    }

    public boolean canCreateRole(Role currentUserRole, Role targetRole) {
        return currentUserRole.getRank() > targetRole.getRank();
    }

    public AuthResponse authenticate(AuthRequest request) {
        User user;
        if (request.getIsNumber()) {
            user = userRepository.findByNumber(request.getNumber()).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        } else {
            user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        }
        boolean checkPassword = userPasswordChecker(user, request.getPassword());
        if (checkPassword)
            return createToken(user);
        else
            throw new UsernameNotFoundException("Password Incorrect");

    }

    public boolean userPasswordChecker(User user, String password) {
        return passwordEncoder.matches(password.trim(), user.getPassword());
    }

    public AuthResponse createToken(User user) {
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveToken(user, jwtToken);
        return AuthResponse.builder().accsesToken(jwtToken).refreshToken(refreshToken).build();
    }


    private void saveToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .expired(false)
                .revoked(false)
                .tokenType(BEARER)
                .token(jwtToken)
                .build();
        tokenRepository.save(token);
    }

}