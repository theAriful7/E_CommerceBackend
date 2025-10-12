package com.My.E_CommerceApp.Controller;

import com.My.E_CommerceApp.DTO.RequestDTO.CategoryRequestDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.CategoryResponseDTO;
import com.My.E_CommerceApp.Service.AddressService;
import com.My.E_CommerceApp.Service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // ✅ Create new category
    @PostMapping
    public CategoryResponseDTO createCategory(@RequestBody CategoryRequestDTO dto) {
        return categoryService.save(dto);
    }

    // ✅ Get category by ID
    @GetMapping("/{id}")
    public CategoryResponseDTO getCategoryById(@PathVariable Long id) {
        return categoryService.findById(id);
    }

    // ✅ Get all categories
    @GetMapping
    public List<CategoryResponseDTO> getAllCategories() {
        return categoryService.findAll();
    }

    // ✅ Delete category
    @DeleteMapping("/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return "Category deleted successfully!";
    }
}
