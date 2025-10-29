package com.My.E_CommerceApp.Controller;

import com.My.E_CommerceApp.DTO.RequestDTO.AddressRequestDTO;
import com.My.E_CommerceApp.DTO.RequestDTO.ReviewRequestDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.AddressResponseDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.ReviewResponseDTO;
import com.My.E_CommerceApp.Service.AddressService;
import com.My.E_CommerceApp.Service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
//@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class AddressController {

    private final ReviewService reviewService;

    // ✅ Create Review - HTTP 201
    @PostMapping
    public ResponseEntity<ReviewResponseDTO> createReview(@RequestBody ReviewRequestDTO dto) {
        ReviewResponseDTO response = reviewService.createReview(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ✅ Get All Reviews - HTTP 200
    @GetMapping
    public ResponseEntity<List<ReviewResponseDTO>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    // ✅ Get Review by ID - HTTP 200
    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> getReviewById(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }

    // ✅ Get Reviews by Product - HTTP 200
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getReviewsByProduct(productId));
    }

    // ✅ Get Reviews by User - HTTP 200
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.getReviewsByUser(userId));
    }

    // ✅ Get Product Average Rating - HTTP 200
    @GetMapping("/product/{productId}/average-rating")
    public ResponseEntity<Double> getProductAverageRating(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getProductAverageRating(productId));
    }

    // ✅ Update Review - HTTP 200
    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> updateReview(
            @PathVariable Long id,
            @RequestBody ReviewRequestDTO dto) {
        return ResponseEntity.ok(reviewService.updateReview(id, dto));
    }

    // ✅ Delete Review - HTTP 204
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
