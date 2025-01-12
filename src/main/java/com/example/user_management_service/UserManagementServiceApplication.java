package com.example.user_management_service;

import com.example.user_management_service.model.User;
import com.example.user_management_service.repository.UserRepository;
import com.example.user_management_service.role.Role;
import com.example.user_management_service.role.UserStatus;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDate;

@SpringBootApplication
public class UserManagementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserManagementServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner createChiefUser(UserRepository userRepository) {
        return args -> {
            try {
                User chiefUser = new User();
                chiefUser.setFirstName("Samandar");
                chiefUser.setLastName("Gaybullayev");
                chiefUser.setMiddleName("");
                chiefUser.setDateOfBirth(LocalDate.of(1980, 1, 1));
                chiefUser.setStatus(UserStatus.ENABLED);
                chiefUser.setPassword(new BCryptPasswordEncoder().encode("sardor22"));
                chiefUser.setRole(Role.CHIEF);
                chiefUser.setRoleRank(Role.CHIEF.getRank());
                chiefUser.setPhoneNumber("930530732");
                chiefUser.setNumber("998930530732");
                chiefUser.setPhonePrefix("+998");
                String userId= String.valueOf(userRepository.save(chiefUser).getUserId());
                System.out.println("Chief user created successfully! chiefId: "+userId);
            }catch (Exception e){
                e.printStackTrace();
            }

        };
    }

}
