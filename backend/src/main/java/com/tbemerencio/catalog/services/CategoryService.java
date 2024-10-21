package com.tbemerencio.catalog.services;

import com.tbemerencio.catalog.controllers.dtos.CategoryDTO;
import com.tbemerencio.catalog.entities.Category;
import com.tbemerencio.catalog.repositories.CategoryRepository;
import com.tbemerencio.catalog.services.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll(){
        return categoryRepository.findAll().stream()
                .map(CategoryDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryDTO findByID(Long id) {
        Optional<Category> entityOPT = categoryRepository.findById(id);
        Category entity = entityOPT.orElseThrow(() ->
                new EntityNotFoundException("ID not found [" + id + "]"));
        return new CategoryDTO(entity);
    }
}