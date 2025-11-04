package com.My.E_CommerceApp.Repository;

import com.My.E_CommerceApp.Entity.Vendor;
import com.My.E_CommerceApp.Enum.VendorStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorRepo extends JpaRepository<Vendor, Long> {

    // 🔹 BASIC FETCH OPERATIONS
    Optional<Vendor> findByShopNameIgnoreCase(String shopName);
    Optional<Vendor> findByBusinessEmailIgnoreCase(String businessEmail);
    Optional<Vendor> findByTaxNumber(String taxNumber);

    boolean existsByShopNameIgnoreCase(String shopName);
    boolean existsByBusinessEmailIgnoreCase(String businessEmail);
    boolean existsByTaxNumber(String taxNumber);

    // 🔹 STATUS-BASED QUERIES
    List<Vendor> findByVendorStatus(VendorStatus status);
    Page<Vendor> findByVendorStatus(VendorStatus status, Pageable pageable);

    List<Vendor> findByVendorStatusIn(List<VendorStatus> statuses);
    Page<Vendor> findByVendorStatusIn(List<VendorStatus> statuses, Pageable pageable);

    // 🔹 USER-RELATED QUERIES
    Optional<Vendor> findByUserId(Long userId);
    Optional<Vendor> findByUserEmailIgnoreCase(String email);

    @Query("SELECT v FROM Vendor v WHERE v.user.id IN :userIds")
    List<Vendor> findByUserIds(@Param("userIds") List<Long> userIds);

    // 🔹 SEARCH & FILTER OPERATIONS
    @Query("SELECT v FROM Vendor v WHERE " +
            "LOWER(v.shopName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(v.shopDescription) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(v.businessEmail) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Vendor> searchVendors(@Param("keyword") String keyword);

    @Query("SELECT v FROM Vendor v WHERE " +
            "LOWER(v.shopName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(v.shopDescription) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(v.businessEmail) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Vendor> searchVendors(@Param("keyword") String keyword, Pageable pageable);

    // 🔹 RATING & PERFORMANCE QUERIES
    List<Vendor> findByAverageRatingGreaterThanEqual(Double minRating);
    List<Vendor> findByAverageRatingBetween(Double minRating, Double maxRating);

    @Query("SELECT v FROM Vendor v WHERE v.averageRating >= :minRating AND v.vendorStatus = 'ACTIVE'")
    List<Vendor> findActiveVendorsWithMinRating(@Param("minRating") Double minRating);

    // 🔹 COUNT & ANALYTICS
    Long countByVendorStatus(VendorStatus status);

    @Query("SELECT COUNT(v) FROM Vendor v WHERE v.vendorStatus IN :statuses")
    Long countByVendorStatusIn(@Param("statuses") List<VendorStatus> statuses);

    @Query("SELECT COUNT(v) FROM Vendor v WHERE v.vendorStatus = 'ACTIVE'")
    Long countActiveVendors();

    // 🔹 VALIDATION QUERIES (for updates)
    boolean existsByShopNameIgnoreCaseAndIdNot(String shopName, Long id);
    boolean existsByBusinessEmailIgnoreCaseAndIdNot(String businessEmail, Long id);
    boolean existsByTaxNumberAndIdNot(String taxNumber, Long id);

    // 🔹 BULK OPERATIONS
    @Query("SELECT v FROM Vendor v WHERE v.id IN :vendorIds")
    List<Vendor> findVendorsByIds(@Param("vendorIds") List<Long> vendorIds);

    @Query("SELECT v FROM Vendor v WHERE v.vendorStatus = 'PENDING_APPROVAL'")
    List<Vendor> findPendingApprovalVendors();

    // 🔹 PERFORMANCE OPTIMIZED QUERIES
    @Query("SELECT v.id, v.shopName, v.averageRating, v.vendorStatus FROM Vendor v WHERE v.vendorStatus = 'ACTIVE'")
    List<Object[]> findActiveVendorsBasicInfo();

    @Query("SELECT v FROM Vendor v JOIN FETCH v.user WHERE v.id = :vendorId")
    Optional<Vendor> findByIdWithUser(@Param("vendorId") Long vendorId);

    // 🔹 ADMIN DASHBOARD QUERIES
    @Query("SELECT v.vendorStatus, COUNT(v) FROM Vendor v GROUP BY v.vendorStatus")
    List<Object[]> countVendorsByStatus();

    @Query("SELECT v FROM Vendor v ORDER BY v.averageRating DESC")
    List<Vendor> findTopRatedVendors(Pageable pageable);
}
