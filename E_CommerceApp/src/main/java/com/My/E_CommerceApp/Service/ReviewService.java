package com.My.E_CommerceApp.Service;

import com.My.E_CommerceApp.DTO.RequestDTO.ReviewRequestDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.ReviewResponseDTO;
import com.My.E_CommerceApp.Entity.Product;
import com.My.E_CommerceApp.Entity.Review;
import com.My.E_CommerceApp.Entity.User;
import com.My.E_CommerceApp.Repository.ProductRepo;
import com.My.E_CommerceApp.Repository.ReviewRepo;
import com.My.E_CommerceApp.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepo reviewRepo;
    private final UserRepo userRepo;
    private final ProductRepo productRepo;

    // 🔹 Create Review
    public ReviewResponseDTO createReview(ReviewRequestDTO dto) {
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepo.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setComment(dto.getComment());
        review.setRating(dto.getRating());
        review.setIsActive(true);

        Review saved = reviewRepo.save(review);
        return convertToResponse(saved);
    }

    // 🔹 Get All Reviews
    public List<ReviewResponseDTO> getAllReviews() {
        return reviewRepo.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // 🔹 Get Reviews by Product
    public List<ReviewResponseDTO> getReviewsByProduct(Long productId) {
        return reviewRepo.findByProductId(productId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // 🔹 Get Reviews by User
    public List<ReviewResponseDTO> getReviewsByUser(Long userId) {
        return reviewRepo.findByUserId(userId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // 🔹 Delete Review
    public void deleteReview(Long id) {
        reviewRepo.deleteById(id);
    }

    // 🔹 Convert Entity to ResponseDTO
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
