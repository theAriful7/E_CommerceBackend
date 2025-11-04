package com.My.E_CommerceApp.Service;

import com.My.E_CommerceApp.DTO.RequestDTO.VendorCreateRequestDTO;
import com.My.E_CommerceApp.DTO.RequestDTO.VendorRequestDTO;
import com.My.E_CommerceApp.DTO.RequestDTO.VendorUpdateRequestDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.UserBasicResponseDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.VendorResponseDTO;
import com.My.E_CommerceApp.Entity.User;
import com.My.E_CommerceApp.Entity.Vendor;
import com.My.E_CommerceApp.Enum.Role;
import com.My.E_CommerceApp.Enum.VendorStatus;
import com.My.E_CommerceApp.Exception.CustomException.AlreadyExistsException;
import com.My.E_CommerceApp.Exception.CustomException.BusinessValidationException;
import com.My.E_CommerceApp.Exception.CustomException.ResourceNotFoundException;
import com.My.E_CommerceApp.Repository.UserRepo;
import com.My.E_CommerceApp.Repository.VendorRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class VendorService {
    private final VendorRepo vendorRepo;
    private final UserRepo userRepo;
    private final UserService userService;

    // 🔹 VENDOR REGISTRATION OPERATIONS
    public VendorResponseDTO createVendorWithNewUser(VendorCreateRequestDTO vendorCreateRequestDTO) {
        System.out.println("Creating new vendor with user for shop: " + vendorCreateRequestDTO.getShopName());

        validateVendorCreation(vendorCreateRequestDTO);

        // Create User first
        User user = createUserFromVendorRequest(vendorCreateRequestDTO);
        user.setRole(Role.VENDOR_USER);
        User savedUser = userRepo.save(user);

        // Create Vendor profile
        Vendor vendor = createVendorFromRequest(vendorCreateRequestDTO, savedUser);
        Vendor savedVendor = vendorRepo.save(vendor);

        System.out.println("Vendor created successfully with ID: " + savedVendor.getId() + " for user ID: " + savedUser.getId());

        return mapToVendorResponseDTO(savedVendor);
    }

    public VendorResponseDTO createVendorForExistingUser(Long userId, VendorRequestDTO vendorRequestDTO) {
        System.out.println("Creating vendor profile for existing user ID: " + userId);

        User user = userService.findUserById(userId);
        validateUserCanBecomeVendor(user);
        validateVendorProfileCreation(vendorRequestDTO);

        Vendor vendor = createVendorFromRequest(vendorRequestDTO, user);
        vendor.setVendorStatus(VendorStatus.PENDING_APPROVAL);
        Vendor savedVendor = vendorRepo.save(vendor);

        // Update user role to VENDOR_USER
        user.setRole(Role.VENDOR_USER);
        userRepo.save(user);

        System.out.println("Vendor profile created successfully for user ID: " + userId);
        return mapToVendorResponseDTO(savedVendor);
    }

    // 🔹 VENDOR PROFILE OPERATIONS
    @Transactional(readOnly = true)
    public VendorResponseDTO getVendorById(Long vendorId) {
        System.out.println("Fetching vendor by ID: " + vendorId);
        Vendor vendor = findVendorById(vendorId);
        return mapToVendorResponseDTO(vendor);
    }

    @Transactional(readOnly = true)
    public VendorResponseDTO getVendorByUserId(Long userId) {
        System.out.println("Fetching vendor by user ID: " + userId);
        Vendor vendor = vendorRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "user ID", userId));
        return mapToVendorResponseDTO(vendor);
    }

    @Transactional(readOnly = true)
    public VendorResponseDTO getVendorByShopName(String shopName) {
        System.out.println("Fetching vendor by shop name: " + shopName);
        Vendor vendor = vendorRepo.findByShopNameIgnoreCase(shopName)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "shop name", shopName));
        return mapToVendorResponseDTO(vendor);
    }

    @Transactional(readOnly = true)
    public List<VendorResponseDTO> getAllVendors() {
        System.out.println("Fetching all vendors");
        return vendorRepo.findAll().stream()
                .map(this::mapToVendorResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<VendorResponseDTO> getAllVendors(Pageable pageable) {
        System.out.println("Fetching vendors with pagination");
        return vendorRepo.findAll(pageable)
                .map(this::mapToVendorResponseDTO);
    }

    // 🔹 VENDOR STATUS MANAGEMENT
    public VendorResponseDTO updateVendorStatus(Long vendorId, VendorStatus newStatus) {
        System.out.println("Updating vendor status for ID: " + vendorId + " to " + newStatus);

        Vendor vendor = findVendorById(vendorId);

        // Business validation for status transitions
        validateStatusTransition(vendor.getVendorStatus(), newStatus);

        vendor.setVendorStatus(newStatus);
        Vendor updatedVendor = vendorRepo.save(vendor);

        System.out.println("Vendor status updated from " + vendor.getVendorStatus() + " to " + newStatus);
        return mapToVendorResponseDTO(updatedVendor);
    }

    public VendorResponseDTO approveVendor(Long vendorId) {
        System.out.println("Approving vendor with ID: " + vendorId);
        return updateVendorStatus(vendorId, VendorStatus.ACTIVE);
    }

    public VendorResponseDTO suspendVendor(Long vendorId) {
        System.out.println("Suspending vendor with ID: " + vendorId);
        return updateVendorStatus(vendorId, VendorStatus.SUSPENDED);
    }

    public VendorResponseDTO rejectVendor(Long vendorId) {
        System.out.println("Rejecting vendor with ID: " + vendorId);
        return updateVendorStatus(vendorId, VendorStatus.REJECTED);
    }

    // 🔹 VENDOR PROFILE UPDATE
    public VendorResponseDTO updateVendorProfile(Long vendorId, VendorUpdateRequestDTO updateRequestDTO) {
        System.out.println("Updating vendor profile for ID: " + vendorId);

        Vendor vendor = findVendorById(vendorId);
        validateVendorProfileUpdate(vendor, updateRequestDTO);

        updateVendorEntity(vendor, updateRequestDTO);
        Vendor updatedVendor = vendorRepo.save(vendor);

        System.out.println("Vendor profile updated successfully for ID: " + vendorId);
        return mapToVendorResponseDTO(updatedVendor);
    }

    // 🔹 SEARCH & FILTER OPERATIONS
    @Transactional(readOnly = true)
    public List<VendorResponseDTO> searchVendors(String keyword) {
        System.out.println("Searching vendors with keyword: " + keyword);
        return vendorRepo.searchVendors(keyword).stream()
                .map(this::mapToVendorResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<VendorResponseDTO> searchVendors(String keyword, Pageable pageable) {
        System.out.println("Searching vendors with keyword " + keyword + " and pagination");
        return vendorRepo.searchVendors(keyword, pageable)
                .map(this::mapToVendorResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<VendorResponseDTO> getVendorsByStatus(VendorStatus status) {
        System.out.println("Fetching vendors by status: " + status);
        return vendorRepo.findByVendorStatus(status).stream()
                .map(this::mapToVendorResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VendorResponseDTO> getActiveVendors() {
        System.out.println("Fetching all active vendors");
        return vendorRepo.findByVendorStatus(VendorStatus.ACTIVE).stream()
                .map(this::mapToVendorResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VendorResponseDTO> getPendingApprovalVendors() {
        System.out.println("Fetching pending approval vendors");
        return vendorRepo.findPendingApprovalVendors().stream()
                .map(this::mapToVendorResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VendorResponseDTO> getTopRatedVendors(Pageable pageable) {
        System.out.println("Fetching top rated vendors");
        return vendorRepo.findTopRatedVendors(pageable).stream()
                .map(this::mapToVendorResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VendorResponseDTO> getVendorsWithMinRating(Double minRating) {
        System.out.println("Fetching vendors with minimum rating: " + minRating);
        return vendorRepo.findByAverageRatingGreaterThanEqual(minRating).stream()
                .map(this::mapToVendorResponseDTO)
                .collect(Collectors.toList());
    }

    // 🔹 VALIDATION METHODS
    private void validateVendorCreation(VendorCreateRequestDTO vendorCreateRequestDTO) {
        if (userRepo.existsByEmailIgnoreCase(vendorCreateRequestDTO.getEmail())) {
            throw new AlreadyExistsException("Email already exists: " + vendorCreateRequestDTO.getEmail());
        }

        if (vendorRepo.existsByShopNameIgnoreCase(vendorCreateRequestDTO.getShopName())) {
            throw new AlreadyExistsException("Shop name already exists: " + vendorCreateRequestDTO.getShopName());
        }

        if (vendorCreateRequestDTO.getTaxNumber() != null &&
                vendorRepo.existsByTaxNumber(vendorCreateRequestDTO.getTaxNumber())) {
            throw new AlreadyExistsException("Tax number already exists: " + vendorCreateRequestDTO.getTaxNumber());
        }
    }

    private void validateVendorProfileCreation(VendorRequestDTO vendorRequestDTO) {
        if (vendorRepo.existsByShopNameIgnoreCase(vendorRequestDTO.getShopName())) {
            throw new AlreadyExistsException("Shop name already exists: " + vendorRequestDTO.getShopName());
        }

        if (vendorRequestDTO.getTaxNumber() != null &&
                vendorRepo.existsByTaxNumber(vendorRequestDTO.getTaxNumber())) {
            throw new AlreadyExistsException("Tax number already exists: " + vendorRequestDTO.getTaxNumber());
        }
    }

    private void validateVendorProfileUpdate(Vendor vendor, VendorUpdateRequestDTO updateRequestDTO) {
        if (updateRequestDTO.getShopName() != null &&
                !vendor.getShopName().equalsIgnoreCase(updateRequestDTO.getShopName()) &&
                vendorRepo.existsByShopNameIgnoreCaseAndIdNot(updateRequestDTO.getShopName(), vendor.getId())) {
            throw new AlreadyExistsException("Shop name already exists: " + updateRequestDTO.getShopName());
        }

        if (updateRequestDTO.getTaxNumber() != null &&
                !vendor.getTaxNumber().equals(updateRequestDTO.getTaxNumber()) &&
                vendorRepo.existsByTaxNumberAndIdNot(updateRequestDTO.getTaxNumber(), vendor.getId())) {
            throw new AlreadyExistsException("Tax number already exists: " + updateRequestDTO.getTaxNumber());
        }
    }

    private void validateUserCanBecomeVendor(User user) {
        if (!user.getIsActive()) {
            throw new BusinessValidationException("User account is not active");
        }

        if (vendorRepo.findByUserId(user.getId()).isPresent()) {
            throw new AlreadyExistsException("Vendor profile already exists for this user");
        }

        if (user.getRole() == Role.ADMIN) {
            throw new BusinessValidationException("Admin users cannot become vendors");
        }
    }

    private void validateStatusTransition(VendorStatus currentStatus, VendorStatus newStatus) {
        if (currentStatus == VendorStatus.REJECTED && newStatus == VendorStatus.ACTIVE) {
            throw new BusinessValidationException("Cannot activate a rejected vendor. Please review application.");
        }
    }

    // 🔹 UTILITY METHODS
    public Vendor findVendorById(Long vendorId) {
        return vendorRepo.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "ID", vendorId));
    }

    private User createUserFromVendorRequest(VendorCreateRequestDTO requestDTO) {
        User user = new User();
        user.setFullName(requestDTO.getFullName());
        user.setEmail(requestDTO.getEmail());
        user.setPhone(requestDTO.getPhone());
        user.setPassword(requestDTO.getPassword());
        user.setProfileImage(requestDTO.getProfileImage());
        user.setGender(requestDTO.getGender());
        user.setBio(requestDTO.getBio());
        return user;
    }

    private Vendor createVendorFromRequest(VendorCreateRequestDTO requestDTO, User user) {
        Vendor vendor = new Vendor();
        vendor.setShopName(requestDTO.getShopName());
        vendor.setShopDescription(requestDTO.getShopDescription());
        vendor.setShopLogo(requestDTO.getShopLogo());
        vendor.setBusinessEmail(requestDTO.getBusinessEmail());
        vendor.setBusinessPhone(requestDTO.getBusinessPhone());
        vendor.setTaxNumber(requestDTO.getTaxNumber());
        vendor.setUser(user);
        vendor.setVendorStatus(VendorStatus.PENDING_APPROVAL);
        return vendor;
    }

    private Vendor createVendorFromRequest(VendorRequestDTO requestDTO, User user) {
        Vendor vendor = new Vendor();
        vendor.setShopName(requestDTO.getShopName());
        vendor.setShopDescription(requestDTO.getShopDescription());
        vendor.setShopLogo(requestDTO.getShopLogo());
        vendor.setBusinessEmail(requestDTO.getBusinessEmail());
        vendor.setBusinessPhone(requestDTO.getBusinessPhone());
        vendor.setTaxNumber(requestDTO.getTaxNumber());
        vendor.setBankAccountDetails(requestDTO.getBankAccountDetails());
        vendor.setUser(user);
        vendor.setVendorStatus(VendorStatus.PENDING_APPROVAL);
        return vendor;
    }

    private void updateVendorEntity(Vendor vendor, VendorUpdateRequestDTO updateRequestDTO) {
        if (updateRequestDTO.getShopName() != null) {
            vendor.setShopName(updateRequestDTO.getShopName());
        }
        if (updateRequestDTO.getShopDescription() != null) {
            vendor.setShopDescription(updateRequestDTO.getShopDescription());
        }
        if (updateRequestDTO.getShopLogo() != null) {
            vendor.setShopLogo(updateRequestDTO.getShopLogo());
        }
        if (updateRequestDTO.getBusinessEmail() != null) {
            vendor.setBusinessEmail(updateRequestDTO.getBusinessEmail());
        }
        if (updateRequestDTO.getBusinessPhone() != null) {
            vendor.setBusinessPhone(updateRequestDTO.getBusinessPhone());
        }
        if (updateRequestDTO.getTaxNumber() != null) {
            vendor.setTaxNumber(updateRequestDTO.getTaxNumber());
        }
        if (updateRequestDTO.getBankAccountDetails() != null) {
            vendor.setBankAccountDetails(updateRequestDTO.getBankAccountDetails());
        }
    }

    // 🔹 MAPPING METHODS (Manual DTO Mapping)
    private VendorResponseDTO mapToVendorResponseDTO(Vendor vendor) {
        VendorResponseDTO responseDTO = new VendorResponseDTO();
        responseDTO.setId(vendor.getId());
        responseDTO.setShopName(vendor.getShopName());
        responseDTO.setShopDescription(vendor.getShopDescription());
        responseDTO.setShopLogo(vendor.getShopLogo());
        responseDTO.setAverageRating(vendor.getAverageRating());
        responseDTO.setBusinessEmail(vendor.getBusinessEmail());
        responseDTO.setBusinessPhone(vendor.getBusinessPhone());
        responseDTO.setTaxNumber(vendor.getTaxNumber());
        responseDTO.setVendorStatus(vendor.getVendorStatus());
        responseDTO.setCreatedAt(vendor.getCreatedAt());
        responseDTO.setUpdatedAt(vendor.getUpdatedAt());

        // Map user info
        if (vendor.getUser() != null) {
            UserBasicResponseDTO userDTO = new UserBasicResponseDTO();
            userDTO.setId(vendor.getUser().getId());
            userDTO.setFullName(vendor.getUser().getFullName());
            userDTO.setEmail(vendor.getUser().getEmail());
            userDTO.setPhone(vendor.getUser().getPhone());
            userDTO.setProfileImage(vendor.getUser().getProfileImage());
            responseDTO.setUser(userDTO);
        }

        return responseDTO;
    }

    // 🔹 BUSINESS LOGIC METHODS
    @Transactional(readOnly = true)
    public boolean isVendorActive(Long vendorId) {
        return vendorRepo.findById(vendorId)
                .map(vendor -> vendor.getVendorStatus() == VendorStatus.ACTIVE)
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public Long getActiveVendorsCount() {
        return vendorRepo.countActiveVendors();
    }

    @Transactional(readOnly = true)
    public Long getVendorsCountByStatus(VendorStatus status) {
        return vendorRepo.countByVendorStatus(status);
    }

    @Transactional(readOnly = true)
    public Long getPendingApprovalCount() {
        return vendorRepo.countByVendorStatus(VendorStatus.PENDING_APPROVAL);
    }

    public void updateVendorRating(Long vendorId, Double newRating) {
        Vendor vendor = findVendorById(vendorId);
        vendor.setAverageRating(newRating);
        vendorRepo.save(vendor);
        System.out.println("Vendor rating updated to " + newRating + " for vendor ID: " + vendorId);
    }

    @Transactional(readOnly = true)
    public boolean vendorExistsByShopName(String shopName) {
        return vendorRepo.existsByShopNameIgnoreCase(shopName);
    }

    @Transactional(readOnly = true)
    public boolean vendorExistsByUserId(Long userId) {
        return vendorRepo.findByUserId(userId).isPresent();
    }

    @Transactional(readOnly = true)
    public List<VendorResponseDTO> getVendorsByStatusList(List<VendorStatus> statuses) {
        System.out.println("Fetching vendors by status list: " + statuses);
        return vendorRepo.findByVendorStatusIn(statuses).stream()
                .map(this::mapToVendorResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<VendorResponseDTO> getVendorsByStatus(VendorStatus status, Pageable pageable) {
        System.out.println("Fetching vendors by status " + status + " with pagination");
        return vendorRepo.findByVendorStatus(status, pageable)
                .map(this::mapToVendorResponseDTO);
    }
}
