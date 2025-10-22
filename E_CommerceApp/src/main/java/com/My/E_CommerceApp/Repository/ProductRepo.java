package com.My.E_CommerceApp.Repository;

import com.My.E_CommerceApp.Entity.Address;
import com.My.E_CommerceApp.Entity.Product;
import com.My.E_CommerceApp.Enum.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
