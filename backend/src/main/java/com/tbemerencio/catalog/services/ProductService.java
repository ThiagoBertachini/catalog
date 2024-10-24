package com.tbemerencio.catalog.services;

import com.tbemerencio.catalog.controllers.dtos.ProductDTO;
import com.tbemerencio.catalog.entities.Category;
import com.tbemerencio.catalog.entities.Product;
import com.tbemerencio.catalog.repositories.CategoryRepository;
import com.tbemerencio.catalog.repositories.ProductRepository;
import com.tbemerencio.catalog.services.exceptions.DataBaseIntegrityException;
import com.tbemerencio.catalog.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged(PageRequest pageRequest) {
        return productRepository.findAll(pageRequest).map(ProductDTO::new);
    }

    @Transactional(readOnly = true)
    public ProductDTO findByID(Long id) {
        Optional<Product> entityOPT = productRepository.findById(id);
        Product entity = entityOPT.orElseThrow(() ->
                new ResourceNotFoundException("ID not found [" + id + "]"));
        return new ProductDTO(entity, entity.getCategories());
    }

    @Transactional
    public ProductDTO create(ProductDTO productDTO) {
        Product entity = new Product();
        entity = productRepository.save(produtoDTOToProduct(productDTO, entity));
        return new ProductDTO(entity);
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO productRequestDTO) {
        try {
            Product productEntity = productRepository.getOne(id);
            return new ProductDTO(productRepository.save(produtoDTOToProduct(productRequestDTO, productEntity)));
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("ID not found " + id);
        }
    }

    public void delete(Long id) {
        try {
            productRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("ID not found " + id);
        } catch (DataIntegrityViolationException e) {
            throw new DataBaseIntegrityException("Deletion of related items not allowed");
        }
    }

    private Product produtoDTOToProduct(ProductDTO productDTO, Product entity) {
        entity.setName(productDTO.getName());
        entity.setDescription(productDTO.getDescription());
        entity.setPrice(productDTO.getPrice());
        entity.setImgUrl(productDTO.getImgUrl());
        entity.setDate(productDTO.getDate());

        if (!CollectionUtils.isEmpty(productDTO.getCategories())) {
            entity.getCategories().clear();

            productDTO.getCategories().forEach(categoryDTO -> {
                Category category = categoryRepository.findById(categoryDTO.getId()).orElseThrow(
                        () -> new ResourceNotFoundException("Category doesen't exists -> " + categoryDTO.getId()));
                entity.getCategories().add(category);
            });
        }
        return entity;
    }
}