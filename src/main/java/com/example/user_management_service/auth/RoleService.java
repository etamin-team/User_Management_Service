package com.example.user_management_service.auth;

import com.example.user_management_service.role.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * RoleService
 * Provides methods to retrieve the current user's role based on security context.
 */
@Service
public class RoleService {

    /**
     * Retrieves the current user's role from the security context.
     *
     * @return the user's Role
     */
    public Role getCurrentUserRole() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found.");
        }

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            try {
                return Role.valueOf(authority.getAuthority().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("Invalid role: " + authority.getAuthority());
            }
        }

        throw new IllegalStateException("No roles found for the current user.");
    }
}
