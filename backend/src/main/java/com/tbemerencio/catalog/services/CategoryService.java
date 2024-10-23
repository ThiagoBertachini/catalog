package com.tbemerencio.catalog.services;

import com.tbemerencio.catalog.controllers.dtos.CategoryDTO;
import com.tbemerencio.catalog.entities.Category;
import com.tbemerencio.catalog.repositories.CategoryRepository;
import com.tbemerencio.catalog.services.exceptions.CategoryNotFoundException;
import com.tbemerencio.catalog.services.exceptions.DataBaseIntegrityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<CategoryDTO> findAllPaged(PageRequest pageRequest) {
        return categoryRepository.findAll(pageRequest).map(CategoryDTO::new);
    }

    @Transactional(readOnly = true)
    public CategoryDTO findByID(Long id) {
        Optional<Category> entityOPT = categoryRepository.findById(id);
        Category entity = entityOPT.orElseThrow(() ->
                new CategoryNotFoundException("ID not found [" + id + "]"));
        return new CategoryDTO(entity);
    }

    @Transactional
    public CategoryDTO create(CategoryDTO categoryDTO) {
        return new CategoryDTO(categoryRepository.save(new Category(categoryDTO)));
    }

    @Transactional
    public CategoryDTO update(Long id, CategoryDTO categoyRequestDTO) {
        try {
            Category categoryEntityDTO = categoryRepository.getOne(id);
            categoryEntityDTO.setName(categoyRequestDTO.getName());
            return new CategoryDTO(categoryRepository.save(categoryEntityDTO));
        } catch (EntityNotFoundException e) {
            throw new CategoryNotFoundException("ID not found " + id);
        }
    }

    public void delete(Long id) {
        try {
            categoryRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new CategoryNotFoundException("ID not found " + id);
        } catch (DataIntegrityViolationException e) {
            throw new DataBaseIntegrityException("Deletion of related items not allowed");
        }
    }
}