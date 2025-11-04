package com.My.E_CommerceApp.Entity;


import com.My.E_CommerceApp.Enum.VendorStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vendors")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Vendor extends Base {

    @Column(name = "shop_name", nullable = false, unique = true)
    private String shopName;

    @Column(name = "shop_description", length = 500)
    private String shopDescription;

    @Column(name = "shop_logo")
    private String shopLogo;

    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    // Business information
    @Column(name = "business_email")
    private String businessEmail;

    @Column(name = "business_phone")
    private String businessPhone;

    @Column(name = "tax_number")
    private String taxNumber;

    @Column(name = "bank_account_details")
    private String bankAccountDetails;

    // Vendor status (ACTIVE, SUSPENDED, PENDING_APPROVAL, etc.)
    @Enumerated(EnumType.STRING)
    @Column(name = "vendor_status", nullable = false)
    private VendorStatus vendorStatus = VendorStatus.PENDING_APPROVAL;

    // One-to-One relationship with User (the owner of this vendor account)
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // A vendor can have multiple products
    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();

    // Additional vendor-specific relationships can be added here
    // For example: vendor payments, vendor settlements, etc.
}