package com.example.user_management_service.role;

/**
 * Role Enum
 * Date: 11/19/2024
 * Author: Sardor Tokhirov
 * Description: Represents user roles with ranks for privilege comparison.
 */
public enum Role {
    CHIEF(5),
    SUPERADMIN(4),
    ADMIN(3),
    MANAGER(2),
    DOCTOR(1),
    PATIENT(0);

    private final int rank;

    Role(int rank) {
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }

    public String getRoleName() {
        return this.name().toLowerCase();  // 'chief', 'superadmin', etc.
    }

    // Optional method to check if this role has higher or lower rank
    public boolean hasHigherRankThan(Role otherRole) {
        return this.rank > otherRole.rank;
    }

    public boolean hasLowerRankThan(Role otherRole) {
        return this.rank < otherRole.rank;
    }
}
