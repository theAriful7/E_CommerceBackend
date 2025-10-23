package com.My.E_CommerceApp.Service;

import com.My.E_CommerceApp.DTO.RequestDTO.ReviewRequestDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.ReviewResponseDTO;
import com.My.E_CommerceApp.Entity.Product;
import com.My.E_CommerceApp.Entity.Review;
import com.My.E_CommerceApp.Entity.User;
import com.My.E_CommerceApp.Exception.CustomException.OperationFailedException;
import com.My.E_CommerceApp.Exception.CustomException.ResourceNotFoundException;
import com.My.E_CommerceApp.Exception.CustomException.ValidationException;
import com.My.E_CommerceApp.Repository.ProductRepo;
import com.My.E_CommerceApp.Repository.ReviewRepo;
import com.My.E_CommerceApp.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepo reviewRepo;
    private final UserRepo userRepo;
    private final ProductRepo productRepo;

    // ✅ Create Review with validation
    @Transactional
    public ReviewResponseDTO createReview(ReviewRequestDTO dto) {
        try {
            // Validate user exists
            User user = userRepo.findById(dto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", dto.getUserId()));

            // Validate product exists
            Product product = productRepo.findById(dto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", dto.getProductId()));

            // Validate rating (1-5)
            if (dto.getRating() == null || dto.getRating() < 1 || dto.getRating() > 5) {
                throw new ValidationException("rating", "Rating must be between 1 and 5");
            }

            // Validate comment
            if (dto.getComment() == null || dto.getComment().trim().isEmpty()) {
                throw new ValidationException("comment", "Comment is required");
            }

            // Check if user already reviewed this product
            boolean alreadyReviewed = reviewRepo.findByUserId(dto.getUserId()).stream()
                    .anyMatch(review -> review.getProduct().getId().equals(dto.getProductId()));

            if (alreadyReviewed) {
                throw new OperationFailedException(
                        "Create review",
                        "You have already reviewed this product"
                );
            }

            Review review = new Review();
            review.setUser(user);
            review.setProduct(product);
            review.setComment(dto.getComment().trim());
            review.setRating(dto.getRating());
            review.setIsActive(true);

            Review saved = reviewRepo.save(review);

            // Update product average rating
            updateProductAverageRating(product.getId());

            return convertToResponse(saved);
        } catch (Exception ex) {
            throw new OperationFailedException("Create review", ex.getMessage());
        }
    }

    // ✅ Get All Reviews
    public List<ReviewResponseDTO> getAllReviews() {
        try {
            return reviewRepo.findAll()
                    .stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new OperationFailedException("Retrieve all reviews", ex.getMessage());
        }
    }

    // ✅ Get Reviews by Product
    public List<ReviewResponseDTO> getReviewsByProduct(Long productId) {
        try {
            // Verify product exists
            if (!productRepo.existsById(productId)) {
                throw new ResourceNotFoundException("Product", "id", productId);
            }

            return reviewRepo.findByProductId(productId)
                    .stream()
                    .filter(Review::getIsActive) // Only active reviews
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new OperationFailedException("Retrieve product reviews", ex.getMessage());
        }
    }

    // ✅ Get Reviews by User
    public List<ReviewResponseDTO> getReviewsByUser(Long userId) {
        try {
            // Verify user exists
            if (!userRepo.existsById(userId)) {
                throw new ResourceNotFoundException("User", "id", userId);
            }

            return reviewRepo.findByUserId(userId)
                    .stream()
                    .filter(Review::getIsActive) // Only active reviews
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new OperationFailedException("Retrieve user reviews", ex.getMessage());
        }
    }

    // ✅ Get Review by ID
    public ReviewResponseDTO getReviewById(Long id) {
        Review review = reviewRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));

        if (!review.getIsActive()) {
            throw new ResourceNotFoundException("Review", "id", id);
        }

        return convertToResponse(review);
    }

    // ✅ Update Review
    @Transactional
    public ReviewResponseDTO updateReview(Long id, ReviewRequestDTO dto) {
        try {
            Review existing = reviewRepo.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));

            // Validate rating if provided
            if (dto.getRating() != null) {
                if (dto.getRating() < 1 || dto.getRating() > 5) {
                    throw new ValidationException("rating", "Rating must be between 1 and 5");
                }
                existing.setRating(dto.getRating());
            }

            // Update comment if provided
            if (dto.getComment() != null && !dto.getComment().trim().isEmpty()) {
                existing.setComment(dto.getComment().trim());
            }

            Review updated = reviewRepo.save(existing);

            // Update product average rating
            updateProductAverageRating(updated.getProduct().getId());

            return convertToResponse(updated);
        } catch (Exception ex) {
            throw new OperationFailedException("Update review", ex.getMessage());
        }
    }

    // ✅ Soft Delete Review (Set inactive instead of hard delete)
    @Transactional
    public void deleteReview(Long id) {
        try {
            Review review = reviewRepo.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));

            review.setIsActive(false);
            reviewRepo.save(review);

            // Update product average rating
            updateProductAverageRating(review.getProduct().getId());
        } catch (Exception ex) {
            throw new OperationFailedException("Delete review", ex.getMessage());
        }
    }

    // ✅ Get Product Average Rating
    public Double getProductAverageRating(Long productId) {
        try {
            List<Review> activeReviews = reviewRepo.findByProductId(productId)
                    .stream()
                    .filter(Review::getIsActive)
                    .collect(Collectors.toList());

            if (activeReviews.isEmpty()) {
                return 0.0;
            }

            double average = activeReviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);

            return Math.round(average * 10.0) / 10.0; // Round to 1 decimal
        } catch (Exception ex) {
            throw new OperationFailedException("Calculate average rating", ex.getMessage());
        }
    }

    // ✅ Helper method to update product average rating
    private void updateProductAverageRating(Long productId) {
        try {
            Double averageRating = getProductAverageRating(productId);
            Product product = productRepo.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

            // If your Product entity has an averageRating field, update it here
            // product.setAverageRating(averageRating);
            // productRepo.save(product);
        } catch (Exception ex) {
            // Log error but don't throw - this is a background update
            System.err.println("Failed to update product average rating: " + ex.getMessage());
        }
    }

    // ✅ Convert Entity to ResponseDTO
    private ReviewResponseDTO convertToResponse(Review review) {
        ReviewResponseDTO dto = new ReviewResponseDTO();
        dto.setId(review.getId());
        dto.setComment(review.getComment());
        dto.setRating(review.getRating());
        dto.setUserName(review.getUser().getFullName());
        dto.setProductName(review.getProduct().getName());
        dto.setIsActive(review.getIsActive());
        return dto;
    }
}
