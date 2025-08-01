package com.example.user_management_service.model;

import com.example.user_management_service.role.Role;
import com.example.user_management_service.role.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "role_rank")
    private int roleRank;


    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "number", unique = true)
    private String number;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_prefix")
    private String phonePrefix;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Column(name = "creator_id")
    private String creatorId;

    @Column(name = "reset_token")
    private Integer resetToken;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "last_update_date")
    private LocalDateTime lastUpdateDate;

    @Column(name = "position")
    private String position;

    @Enumerated(EnumType.STRING)
    @Column(name = "field_name")
    private Field fieldName = Field.NONE;


    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "district_id", referencedColumnName = "id")
    private District district;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "workplace_id", referencedColumnName = "workplace_id")
    private WorkPlace workplace;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.name());
        return Collections.singletonList(authority);
    }

    @Override
    public String getUsername() {
        return userId.toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Role getRole() {
        return role;
    }
    public void setRoleRank(int roleRank) {
        this.roleRank = roleRank;
    }
    public void setRole(Role role) {
        this.role = role;
        this.roleRank = role.getRank();
    }
}
