package com.My.E_CommerceApp.Controller;

import com.My.E_CommerceApp.DTO.RequestDTO.CategoryRequestDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.CategoryResponseDTO;
import com.My.E_CommerceApp.Service.AddressService;
import com.My.E_CommerceApp.Service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class CategoryController {


    private final CategoryService categoryService;


    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(@RequestBody CategoryRequestDTO dto) {
        return ResponseEntity.ok(categoryService.createCategory(dto));
    }


    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }


    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryRequestDTO dto
    ) {
        return ResponseEntity.ok(categoryService.updateCategory(id, dto));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.deleteCategory(id));
    }
}
