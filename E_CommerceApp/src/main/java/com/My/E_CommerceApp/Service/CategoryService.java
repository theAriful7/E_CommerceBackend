package com.My.E_CommerceApp.Service;

import com.My.E_CommerceApp.DTO.RequestDTO.CategoryRequestDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.CategoryResponseDTO;
import com.My.E_CommerceApp.Entity.Category;
import com.My.E_CommerceApp.Repository.AddressRepo;
import com.My.E_CommerceApp.Repository.CategoryRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepo categoryRepo;

    // ✅ Convert Entity → DTO
    private CategoryResponseDTO toDto(Category category) {
        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setCreatedAt(category.getCreatedAt()); // from Base
        dto.setUpdatedAt(category.getUpdatedAt()); // from Base
        return dto;
    }

    // ✅ Convert DTO → Entity
    private Category toEntity(CategoryRequestDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        return category;
    }

    // ✅ Create
    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO dto) {
        boolean exists = categoryRepo.existsByNameIgnoreCase(dto.getName());
        if (exists) throw new RuntimeException("Category name already exists!");

        Category saved = categoryRepo.save(toEntity(dto));
        return toDto(saved);
    }

    // ✅ Get All
    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepo.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ✅ Get By Id
    public CategoryResponseDTO getCategoryById(Long id) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        return toDto(category);
    }

    // ✅ Update
    @Transactional
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO dto) {
        Category existing = categoryRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getDescription() != null) existing.setDescription(dto.getDescription());

        Category updated = categoryRepo.save(existing);
        return toDto(updated);
    }

    // ✅ Delete
    @Transactional
    public String deleteCategory(Long id) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        categoryRepo.delete(category);
        return "Category deleted successfully!";
    }
}
