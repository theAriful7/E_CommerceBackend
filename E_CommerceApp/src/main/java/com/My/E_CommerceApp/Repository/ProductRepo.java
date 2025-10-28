package com.My.E_CommerceApp.Repository;

import com.My.E_CommerceApp.Entity.Product;
import com.My.E_CommerceApp.Enum.ProductStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {
    // Custom method to find products by vendor
    List<Product> findByVendorId(Long vendorId);

    // Custom method to find products by category
    List<Product> findByCategoryId(Long categoryId);

    // Custom method to find products by status
    List<Product> findByStatus(ProductStatus status);

    @Query("SELECT p FROM Product p WHERE " +
            "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
            "(:subCategoryId IS NULL OR p.subCategory.id = :subCategoryId) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:brand IS NULL OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :brand, '%')))")
    List<Product> findByFilters(@Param("categoryId") Long categoryId,
                                @Param("subCategoryId") Long subCategoryId,
                                @Param("minPrice") Double minPrice,
                                @Param("maxPrice") Double maxPrice,
                                @Param("brand") String brand);

    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrBrandContainingIgnoreCase(
            String name, String description, String brand);



    // Trending products with vendor distribution
    @Query(value = """
        WITH ranked_products AS (
            SELECT p.*, 
                   u.shop_name as vendor_name,
                   ROW_NUMBER() OVER (PARTITION BY p.vendor_id ORDER BY 
                       (COALESCE(p.rating, 0) * 0.3 + 
                        COALESCE(p.sales_count, 0) * 0.4 + 
                        COALESCE(p.view_count, 0) * 0.2 + 
                        COALESCE(p.admin_boost, 0) * 0.1 +
                        CASE WHEN p.created_at > CURRENT_DATE - INTERVAL 30 DAY THEN 0.1 ELSE 0 END) DESC
                   ) as vendor_rank,
                   (COALESCE(p.rating, 0) * 0.3 + 
                    COALESCE(p.sales_count, 0) * 0.4 + 
                    COALESCE(p.view_count, 0) * 0.2 + 
                    COALESCE(p.admin_boost, 0) * 0.1 +
                    CASE WHEN p.created_at > CURRENT_DATE - INTERVAL 30 DAY THEN 0.1 ELSE 0 END) as performance_score
            FROM products p
            JOIN users u ON p.vendor_id = u.id
            WHERE p.status = 'APPROVED' 
            AND p.stock > 0
            AND u.account_status = 'ACTIVE'
            AND u.role = 'VENDOR'
        )
        SELECT * FROM ranked_products 
        WHERE vendor_rank <= 2
        ORDER BY performance_score DESC, created_at DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Product> findTrendingProducts(@Param("limit") int limit);

    // Best sellers (pure sales based)
    @Query("""
        SELECT p FROM Product p 
        WHERE p.status = 'APPROVED' 
        AND p.stock > 0 
        AND p.vendor.accountStatus = 'ACTIVE'
        AND p.vendor.role = 'VENDOR'
        ORDER BY p.salesCount DESC, p.rating DESC
        """)
    List<Product> findBestSellingProducts(Pageable pageable);

    // Featured products (admin curated + high performing)
    @Query("""
        SELECT p FROM Product p 
        WHERE p.status = 'APPROVED' 
        AND p.stock > 0 
        AND p.vendor.accountStatus = 'ACTIVE'
        AND p.vendor.role = 'VENDOR'
        AND (p.isFeatured = true OR p.rating >= 4.0 OR p.salesCount >= 50)
        ORDER BY p.isFeatured DESC, p.adminBoost DESC, p.salesCount DESC
        """)
    List<Product> findFeaturedProducts(Pageable pageable);

    // Update product stats
    @Modifying
    @Query("UPDATE Product p SET p.viewCount = COALESCE(p.viewCount, 0) + 1 WHERE p.id = :productId")
    void incrementViewCount(@Param("productId") Long productId);

    @Modifying
    @Query("UPDATE Product p SET p.salesCount = COALESCE(p.salesCount, 0) + :quantity WHERE p.id = :productId")
    void incrementSalesCount(@Param("productId") Long productId, @Param("quantity") int quantity);

    // Get vendor distribution statistics
    @Query("""
        SELECT u.id, COALESCE(u.shopName, u.fullName), 
               COUNT(p.id) as productCount,
               SUM(CASE WHEN p.isFeatured = true THEN 1 ELSE 0 END) as featuredCount
        FROM User u 
        LEFT JOIN u.products p 
        WHERE u.role = 'VENDOR' 
        AND u.accountStatus = 'ACTIVE'
        AND (p IS NULL OR p.status = 'APPROVED')
        GROUP BY u.id, u.shopName, u.fullName
        ORDER BY productCount DESC
        """)
    List<Object[]> getVendorDistributionStats();
}