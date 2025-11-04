package com.My.E_CommerceApp.Service;


import com.My.E_CommerceApp.DTO.RequestDTO.UserRequestDTO;
import com.My.E_CommerceApp.DTO.RequestDTO.UserUpdateRequestDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.AddressResponseDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.UserResponseDTO;
import com.My.E_CommerceApp.Entity.User;
import com.My.E_CommerceApp.Enum.Role;
import com.My.E_CommerceApp.Exception.CustomException.AlreadyExistsException;
import com.My.E_CommerceApp.Exception.CustomException.BusinessValidationException;
import com.My.E_CommerceApp.Exception.CustomException.ResourceNotFoundException;
import com.My.E_CommerceApp.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;

    // 🔹 CREATE OPERATIONS
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        System.out.println("Creating new user with email: " + userRequestDTO.getEmail());

        validateUserCreation(userRequestDTO);

        User user = mapToUserEntity(userRequestDTO);
        user.setPassword(userRequestDTO.getPassword());
        user.setRole(Role.CUSTOMER);

        User savedUser = userRepo.save(user);
        System.out.println("User created successfully with ID: " + savedUser.getId());

        return mapToUserResponseDTO(savedUser);
    }

    // 🔹 READ OPERATIONS
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long userId) {
        System.out.println("Fetching user by ID: " + userId);
        User user = findUserById(userId);
        return mapToUserResponseDTO(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserByEmail(String email) {
        System.out.println("Fetching user by email: " + email);
        User user = userRepo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return mapToUserResponseDTO(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        System.out.println("Fetching all users");
        return userRepo.findAll().stream()
                .map(this::mapToUserResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        System.out.println("Fetching users with pagination");
        return userRepo.findAll(pageable)
                .map(this::mapToUserResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getUsersByRole(Role role) {
        System.out.println("Fetching users by role: " + role);
        return userRepo.findByRole(role).stream()
                .map(this::mapToUserResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getUsersByRole(Role role, Pageable pageable) {
        System.out.println("Fetching users by role " + role + " with pagination");
        return userRepo.findByRole(role, pageable)
                .map(this::mapToUserResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getActiveUsersByRole(Role role) {
        System.out.println("Fetching active users by role: " + role);
        return userRepo.findByRoleAndIsActive(role, true).stream()
                .map(this::mapToUserResponseDTO)
                .collect(Collectors.toList());
    }

    // 🔹 UPDATE OPERATIONS
    public UserResponseDTO updateUser(Long userId, UserUpdateRequestDTO updateRequestDTO) {
        System.out.println("Updating user with ID: " + userId);

        User user = findUserById(userId);
        validateUserUpdate(user, updateRequestDTO);

        updateUserEntity(user, updateRequestDTO);
        User updatedUser = userRepo.save(user);

        System.out.println("User updated successfully with ID: " + userId);
        return mapToUserResponseDTO(updatedUser);
    }

    public UserResponseDTO updateUserRole(Long userId, Role newRole) {
        System.out.println("Updating role for user ID: " + userId + " to " + newRole);

        User user = findUserById(userId);

        // Business validation: Check if user can change to this role
        if (user.getRole() == Role.VENDOR_USER && newRole != Role.VENDOR_USER) {
            throw new BusinessValidationException("Cannot change role from VENDOR_USER. Please contact admin.");
        }

        user.setRole(newRole);
        User updatedUser = userRepo.save(user);

        System.out.println("User role updated successfully from " + user.getRole() + " to " + newRole);
        return mapToUserResponseDTO(updatedUser);
    }

    public void deactivateUser(Long userId) {
        System.out.println("Deactivating user with ID: " + userId);
        User user = findUserById(userId);

        if (user.getRole() == Role.VENDOR_USER) {
            throw new BusinessValidationException("Cannot deactivate vendor user. Please suspend vendor account first.");
        }

        user.setIsActive(false);
        userRepo.save(user);
        System.out.println("User deactivated successfully");
    }

    public void activateUser(Long userId) {
        System.out.println("Activating user with ID: " + userId);
        User user = findUserById(userId);
        user.setIsActive(true);
        userRepo.save(user);
        System.out.println("User activated successfully");
    }

    // 🔹 DELETE OPERATIONS
    public void deleteUser(Long userId) {
        System.out.println("Deleting user with ID: " + userId);
        User user = findUserById(userId);

        // Business validation: Check if user has vendor account
        if (user.getVendor() != null) {
            throw new BusinessValidationException("Cannot delete user with active vendor account. Delete vendor account first.");
        }

        userRepo.delete(user);
        System.out.println("User deleted successfully");
    }

    // 🔹 SEARCH OPERATIONS
    @Transactional(readOnly = true)
    public List<UserResponseDTO> searchUsers(String keyword) {
        System.out.println("Searching users with keyword: " + keyword);
        return userRepo.searchUsers(keyword).stream()
                .map(this::mapToUserResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<UserResponseDTO> searchUsers(String keyword, Pageable pageable) {
        System.out.println("Searching users with keyword " + keyword + " and pagination");
        return userRepo.searchUsers(keyword, pageable)
                .map(this::mapToUserResponseDTO);
    }

    // 🔹 VALIDATION METHODS
    private void validateUserCreation(UserRequestDTO userRequestDTO) {
        if (userRepo.existsByEmailIgnoreCase(userRequestDTO.getEmail())) {
            throw new AlreadyExistsException("Email already exists: " + userRequestDTO.getEmail());
        }

        if (userRequestDTO.getPhone() != null && userRepo.existsByPhone(userRequestDTO.getPhone())) {
            throw new AlreadyExistsException("Phone number already exists: " + userRequestDTO.getPhone());
        }
    }

    private void validateUserUpdate(User user, UserUpdateRequestDTO updateRequestDTO) {
        if (updateRequestDTO.getEmail() != null &&
                !user.getEmail().equalsIgnoreCase(updateRequestDTO.getEmail()) &&
                userRepo.existsByEmailIgnoreCaseAndIdNot(updateRequestDTO.getEmail(), user.getId())) {
            throw new AlreadyExistsException("Email already exists: " + updateRequestDTO.getEmail());
        }

        if (updateRequestDTO.getPhone() != null &&
                !user.getPhone().equals(updateRequestDTO.getPhone()) &&
                userRepo.existsByPhoneAndIdNot(updateRequestDTO.getPhone(), user.getId())) {
            throw new AlreadyExistsException("Phone number already exists: " + updateRequestDTO.getPhone());
        }
    }

    // 🔹 UTILITY METHODS
    public User findUserById(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }

    private void updateUserEntity(User user, UserUpdateRequestDTO updateRequestDTO) {
        if (updateRequestDTO.getFullName() != null) {
            user.setFullName(updateRequestDTO.getFullName());
        }
        if (updateRequestDTO.getEmail() != null) {
            user.setEmail(updateRequestDTO.getEmail());
        }
        if (updateRequestDTO.getPhone() != null) {
            user.setPhone(updateRequestDTO.getPhone());
        }
        if (updateRequestDTO.getProfileImage() != null) {
            user.setProfileImage(updateRequestDTO.getProfileImage());
        }
        if (updateRequestDTO.getGender() != null) {
            user.setGender(updateRequestDTO.getGender());
        }
        if (updateRequestDTO.getBio() != null) {
            user.setBio(updateRequestDTO.getBio());
        }
    }

    // 🔹 MAPPING METHODS (Manual DTO Mapping)
    private User mapToUserEntity(UserRequestDTO userRequestDTO) {
        User user = new User();
        user.setFullName(userRequestDTO.getFullName());
        user.setEmail(userRequestDTO.getEmail());
        user.setPhone(userRequestDTO.getPhone());
        user.setProfileImage(userRequestDTO.getProfileImage());
        user.setGender(userRequestDTO.getGender());
        user.setBio(userRequestDTO.getBio());
        return user;
    }

    private UserResponseDTO mapToUserResponseDTO(User user) {
        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setId(user.getId());
        responseDTO.setFullName(user.getFullName());
        responseDTO.setEmail(user.getEmail());
        responseDTO.setPhone(user.getPhone());
        responseDTO.setRole(user.getRole());
        responseDTO.setProfileImage(user.getProfileImage());
        responseDTO.setGender(user.getGender());
        responseDTO.setBio(user.getBio());
        responseDTO.setIsActive(user.getIsActive());
        responseDTO.setAccountStatus(user.getAccountStatus());
        responseDTO.setCreatedAt(user.getCreatedAt());
        responseDTO.setUpdatedAt(user.getUpdatedAt());

        // Map addresses if needed
        if (user.getAddresses() != null) {
            List<AddressResponseDTO> addressDTOs = user.getAddresses().stream()
                    .map(address -> {
                        AddressResponseDTO addressDTO = new AddressResponseDTO();
                        addressDTO.setId(address.getId());
                        addressDTO.setStreet(address.getStreet());
                        addressDTO.setCity(address.getCity());
                        addressDTO.setState(address.getState());
                        addressDTO.setCountry(address.getCountry());
                        return addressDTO;
                    })
                    .collect(Collectors.toList());
            responseDTO.setAddresses(addressDTOs);
        }

        return responseDTO;
    }

    // 🔹 BUSINESS LOGIC METHODS
    @Transactional(readOnly = true)
    public boolean isUserActive(Long userId) {
        return userRepo.findById(userId)
                .map(User::getIsActive)
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public Long getActiveUsersCount() {
        return userRepo.countActiveUsers();
    }

    @Transactional(readOnly = true)
    public Long getUsersCountByRole(Role role) {
        return userRepo.countByRole(role);
    }

    @Transactional(readOnly = true)
    public boolean userExists(Long userId) {
        return userRepo.existsById(userId);
    }

    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return userRepo.existsByEmailIgnoreCase(email);
    }
}
