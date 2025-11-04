package com.My.E_CommerceApp.Repository;

import com.My.E_CommerceApp.Entity.User;
import com.My.E_CommerceApp.Enum.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    // 🔹 AUTHENTICATION & BASIC FETCH
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByPhone(String phone);

    boolean existsByEmailIgnoreCase(String email);
    boolean existsByPhone(String phone);

    // 🔹 ROLE-BASED QUERIES
    List<User> findByRole(Role role);
    Page<User> findByRole(Role role, Pageable pageable);

    List<User> findByRoleAndIsActive(Role role, Boolean isActive);
    Page<User> findByRoleAndIsActive(Role role, Boolean isActive, Pageable pageable);

    // 🔹 SEARCH FUNCTIONALITY
    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<User> searchUsers(@Param("keyword") String keyword);

    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);

    // 🔹 COUNT & ANALYTICS
    Long countByRole(Role role);
    Long countByRoleAndIsActive(Role role, Boolean isActive);

    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true")
    Long countActiveUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = false")
    Long countInactiveUsers();

    // 🔹 VENDOR-RELATED QUERIES
    @Query("SELECT u FROM User u WHERE u.role = 'VENDOR_USER' AND u.vendor IS NOT NULL")
    List<User> findUsersWithVendorAccounts();

    @Query("SELECT u FROM User u WHERE u.role = 'VENDOR_USER' AND u.vendor IS NULL")
    List<User> findVendorUsersWithoutVendorAccount();

    // 🔹 EMAIL & PHONE VALIDATION (for updates)
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
    boolean existsByPhoneAndIdNot(String phone, Long id);

    // 🔹 BULK OPERATIONS
    @Query("SELECT u FROM User u WHERE u.id IN :userIds")
    List<User> findUsersByIds(@Param("userIds") List<Long> userIds);

    // 🔹 PERFORMANCE OPTIMIZED QUERIES (for dropdowns/lists)
    @Query("SELECT u.id, u.fullName, u.email, u.role FROM User u WHERE u.role = :role AND u.isActive = true")
    List<Object[]> findBasicUserInfoByRole(@Param("role") Role role);
}
