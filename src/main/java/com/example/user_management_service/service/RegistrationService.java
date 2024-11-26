package com.example.user_management_service.service;


import com.example.user_management_service.auth.*;
import com.example.user_management_service.exception.ValidationException;
import com.example.user_management_service.config.JwtService;
import com.example.user_management_service.message.sms.TwilioConfig;
import com.example.user_management_service.model.Country;
import com.example.user_management_service.model.Region;
import com.example.user_management_service.model.User;
import com.example.user_management_service.model.VerificationNumber;
import com.example.user_management_service.model.dto.AuthRequest;
import com.example.user_management_service.model.dto.DoctorSignUpRequest;
import com.example.user_management_service.model.dto.RegisterRequest;
import com.example.user_management_service.repository.TokenRepository;
import com.example.user_management_service.repository.UserRepository;
import com.example.user_management_service.repository.VerificationNumberRepository;
import com.example.user_management_service.role.AuthRandomNumberResponse;
import com.example.user_management_service.role.Role;
import com.example.user_management_service.token.Token;
import com.example.user_management_service.utils.ValidationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static com.example.user_management_service.token.TokenType.BEARER;

/**
 * Date-11/20/2024
 * By Sardor Tokhirov
 * Time-4:49 PM (GMT+5)
 */
@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final CountryRegionService countryRegionService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;



    private final VerificationNumberRepository verificationNumberRepository;

    public AuthRandomNumberResponse signUpDoctor(DoctorSignUpRequest request) {
        VerificationNumber verificationNumber;
        if (!verificationNumberRepository.findByNumber(request.getNumber()).isPresent()) {
            return AuthRandomNumberResponse.WRONG_NUMBER;
        }
        verificationNumber = verificationNumberRepository.findByNumber(request.getNumber()).get();
        if (verificationNumber.getAttempts() > 6) {
            return AuthRandomNumberResponse.TOO_MANY_ATTEMPTS;
        } else if (ChronoUnit.MINUTES.between(LocalDateTime.now(), verificationNumber.getExpirationDate()) > 0 && request.getVerificationNumber().equals(verificationNumber.getRandomNumber())) {
            verificationNumberRepository.deleteByUser(request.getNumber());
         User user=   createUserRequest(
                    new RegisterRequest(
                            request.getFirstName(),
                            request.getLastName(),
                            null,
                            Role.DOCTOR,
                            request.getPassword(),
                            request.getRegion(),
                            request.getCountry(),
                            request.getPhoneNumber(),
                            request.getPhonePrefix(),
                            request.getNumber(),
                            null
                    )
            );
            user.setUserId(UUID.randomUUID());
            user.setCreatorId(String.valueOf(user.getUserId()));
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            userRepository.save(user);
            return AuthRandomNumberResponse.SUCCESS;
        }
        if (ValidationUtils.hasNullFields(request)) {
            ErrorMessage errorMessage = new ErrorMessage(
                    "Error",
                    "One or more required fields are missing: " + getMissingFields(request),
                    request
            );
            throw new ValidationException(errorMessage);
        }
        boolean isNumberPresent = userRepository.findByNumber(request.getNumber()).isPresent();
        if (isNumberPresent) {
            ErrorMessage errorMessage = new ErrorMessage(
                    "Error",
                    "Number is already taken: " + request.getNumber(),
                    request
            );
            throw new ValidationException(errorMessage);
        }
        verificationNumber.setAttempts(verificationNumber.getAttempts() + 1);
        verificationNumberRepository.save(verificationNumber);
        return AuthRandomNumberResponse.INCORRECT_NUMBER;
    }

    public boolean register(RegisterRequest request){
           User user=createUserRequest(request);
           user.setUserId(UUID.randomUUID());
           user.setPassword(passwordEncoder.encode(request.getPassword()));
           userRepository.save(user);
           return true;
    }

    private User createUserRequest(RegisterRequest request) {
        User newUser = new User();
        Country country = countryRegionService.getCountryByName(request.getCountry());
        Region region = countryRegionService.getRegionByName(request.getRegion());
        newUser.setFirstName(request.getFirstName());
        newUser.setLastName(request.getLastName());
        newUser.setPhoneNumber(request.getPhoneNumber());
        newUser.setPhonePrefix(request.getPhonePrefix());
        newUser.setRegion(region);
        newUser.setCountry(country);
        newUser.setPassword(request.getPassword());
        newUser.setCreatorId(request.getCreatorId());
        return newUser;
    }

    private String getMissingFields(DoctorSignUpRequest request) {
        StringBuilder missingFields = new StringBuilder();

        if (request.getFirstName() == null) missingFields.append("firstName, ");
        if (request.getPhoneNumber() == null) missingFields.append("phoneNumber, ");
        if (request.getPhonePrefix() == null) missingFields.append("phonePrefix, ");
        if (request.getLastName() == null) missingFields.append("lastName, ");
        if (request.getPassword() == null) missingFields.append("password, ");
        if (request.getRegion() == null) missingFields.append("region, ");
        if (request.getCountry() == null) missingFields.append("country, ");

        if (missingFields.length() > 0) {
            missingFields.setLength(missingFields.length() - 2);
        }

        return missingFields.toString();
    }
    public boolean isUserWithEmailExists(String email){
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean isUserWithNumberExists(String number){
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
        // Initialize Twilio
//        Twilio.init(accountSid, authToken);
//
//        // Create SMS content
//        String messageBody = "Your verification code is: " + verificationCode;
//
//        // Send SMS
//        Message.creator(
//                new com.twilio.type.PhoneNumber(toPhoneNumber), // To
//                new com.twilio.type.PhoneNumber(twilioPhoneNumber), // From
//                messageBody
//        ).create();

        System.out.println("SMS sent successfully!");
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

//        if (isEmail){
//            //here should send number to email
//        }else {
        sendSMS(number, randomNumber);

//        }
        VerificationNumber verificationNumber = new VerificationNumber(number, randomNumber, expirationDate);
        verificationNumberRepository.save(verificationNumber);
        return true;
    }

    public boolean canCreateRole(Role currentUserRole, Role targetRole) {
        return currentUserRole.getRank() > targetRole.getRank();
    }
    public AuthResponse authenticate(AuthRequest request) {
        User user;
        if (request.getIsNumber()){
            user=userRepository.findByNumber(request.getNumber()).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        }else {
            user=userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
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