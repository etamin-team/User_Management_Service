package com.example.user_management_service.service;

import com.example.user_management_service.model.User;
import com.example.user_management_service.role.Role;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

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

        // Check for an unauthenticated user
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found.");
        }

        // Check for the "ROLE_ANONYMOUS" or invalid roles
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            try {
                return Role.valueOf(authority.getAuthority().toUpperCase().substring(5));
            } catch (IllegalArgumentException e) {
                // Log the error or handle it more gracefully
                System.out.println("Warning: Invalid role detected: " + authority.getAuthority());
                // You can either throw a specific exception or return a default role
                return Role.PATIENT; // Or return a default role, if applicable
            }
        }

        // If no roles are found
        throw new IllegalStateException("No roles found for the current user.");
    }

    public UUID getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check for an unauthenticated user
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found.");
        }

            return ((User) authentication.getPrincipal()).getUserId();
    }
}
