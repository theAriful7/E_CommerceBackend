package com.My.E_CommerceApp.Entity;

import com.My.E_CommerceApp.Enum.ProductStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
@EqualsAndHashCode(callSuper = true)
public class Product extends Base{

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    private Double discount;

    private String brand;

//    @Column(unique = true, nullable = false)
//    private String sku;



    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    // NEW: Optional sub-category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_category_id")
    private SubCategory subCategory; // ADD THIS

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User vendor;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    // NEW: Specifications for the product
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductSpecification> specifications = new ArrayList<>();

    // NEW: Add this method to handle specifications easily
    public void addSpecification(String key, String value, Integer displayOrder) {
        ProductSpecification spec = new ProductSpecification();
        spec.setKey(key);
        spec.setValue(value);
        spec.setDisplayOrder(displayOrder != null ? displayOrder : 0);
        spec.setProduct(this); // Very important - connect specification to product
        this.specifications.add(spec);
    }
}
