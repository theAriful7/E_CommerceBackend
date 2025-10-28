package com.My.E_CommerceApp.Repository;

import com.My.E_CommerceApp.Entity.User;
import com.My.E_CommerceApp.Enum.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByPhone(String phone);

    boolean existsByEmailIgnoreCase(String email);
    boolean existsByPhone(String phone);

    // Find users by role
    List<User> findByRole(Role role);

    // Find active users by role
    List<User> findByRoleAndIsActive(Role role, Boolean isActive);

    List<User> findAllByRole(Role role); // small lists
    Page<User> findAllByRole(Role role, Pageable pageable); // pageable vendor/customer list

    // For search by name or email (for admin)
    Page<User> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email, Pageable pageable);

    // 🔹 NEW METHODS ADDED FOR CUSTOMER/VENDOR MANAGEMENT
    // Count users by role
    Long countByRole(Role role);

    // Count active users by role
    Long countByRoleAndIsActive(Role role, Boolean isActive);

    // Search users by name or email (without pagination for admin panel)
    List<User> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email);
}
