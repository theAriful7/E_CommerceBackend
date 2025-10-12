package com.My.E_CommerceApp.Service;

import com.My.E_CommerceApp.DTO.RequestDTO.CategoryRequestDTO;
import com.My.E_CommerceApp.DTO.ResponseDTO.CategoryResponseDTO;
import com.My.E_CommerceApp.Entity.Category;
import com.My.E_CommerceApp.Repository.AddressRepo;
import com.My.E_CommerceApp.Repository.CategoryRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepo categoryRepo;

    public CategoryService(CategoryRepo categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    // RequestDTO → Entity
    public Category toEntity(CategoryRequestDTO dto) {
        Category c = new Category();
        c.setName(dto.getName());
        c.setDescription(dto.getDescription());
        return c;
    }

    // Entity → ResponseDTO
    public CategoryResponseDTO toDto(Category category) {
        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }

    // ✅ Create / Save category
    public CategoryResponseDTO save(CategoryRequestDTO dto) {
        Category category = toEntity(dto);
        Category saved = categoryRepo.save(category);
        return toDto(saved);
    }

    // ✅ Find category by ID
    public CategoryResponseDTO findById(Long id) {
        return categoryRepo.findById(id)
                .map(this::toDto)
                .orElse(null);
    }

    // ✅ Get all categories
    public List<CategoryResponseDTO> findAll() {
        return categoryRepo.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ✅ Delete category
    public void delete(Long id) {
        categoryRepo.deleteById(id);
    }
}
