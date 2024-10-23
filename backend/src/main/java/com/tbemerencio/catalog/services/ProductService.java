package com.tbemerencio.catalog.services;

import com.tbemerencio.catalog.controllers.dtos.ProductDTO;
import com.tbemerencio.catalog.entities.Product;
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

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

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
        return new ProductDTO(productRepository.save(new Product(productDTO)));
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO productRequestDTO) {
        try {
            Product productEntity = productRepository.getOne(id);
            productEntity = new Product(productRequestDTO);
            return new ProductDTO(productRepository.save(productEntity));
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
}