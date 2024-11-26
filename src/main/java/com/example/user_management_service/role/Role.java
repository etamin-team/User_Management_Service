package com.example.user_management_service.role;

/**
 * Role Enum
 * Date: 11/19/2024
 * Author: Sardor Tokhirov
 * Description: Represents user roles with ranks for privilege comparison.
 */
public enum Role {
    CHIEF(5),         // Highest privilege, rank 5
    SUPERADMIN(4),    // Rank 4
    ADMIN(3),         // Rank 3
    MANAGER(2),       // Rank 2
    DOCTOR(1),        // Rank 1
    PATIENT(0);       // Lowest privilege, rank 0

    private final int rank;

    Role(int rank) {
        this.rank = rank;
    }

    /**
     * Gets the rank of the role.
     * @return the rank as an integer
     */
    public int getRank() {
        return rank;
    }

    /**
     * Checks if this role has a higher rank than the given role.
     * @param otherRole the role to compare against
     * @return true if this role has a higher rank
     */
    public boolean hasHigherRankThan(Role otherRole) {
        return this.rank > otherRole.rank;
    }

    /**
     * Checks if this role has a lower rank than the given role.
     * @param otherRole the role to compare against
     * @return true if this role has a lower rank
     */
    public boolean hasLowerRankThan(Role otherRole) {
        return this.rank < otherRole.rank;
    }

    /**
     * Determines if the current role can assign the target role.
     * @param targetRole the role to be assigned
     * @return true if the current role has a higher rank than the target role
     */
    public boolean canAssign(Role targetRole) {
        return this.rank > targetRole.rank;
    }
}
