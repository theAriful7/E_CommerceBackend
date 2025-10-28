package com.My.E_CommerceApp.Enum;

public enum ProductStatus {
    PENDING,    // Waiting for approval
    ACTIVE,     // Available for sale
    INACTIVE,   // Not available but not deleted
    OUT_OF_STOCK, // Temporarily unavailable
    DISCONTINUED, // Permanently unavailable
    REJECTED, APPROVED;   // Admin rejected the product

    // ✅ ADD THIS METHOD to check if product is available for purchase
    public boolean isActive() {
        return this == ACTIVE;
    }

    // ✅ Optional: Check if product can be displayed
    public boolean isVisible() {
        return this == ACTIVE || this == OUT_OF_STOCK;
    }

    // ✅ Check if product can be purchased
    public boolean canBePurchased() {
        return this == ACTIVE;
    }
}
