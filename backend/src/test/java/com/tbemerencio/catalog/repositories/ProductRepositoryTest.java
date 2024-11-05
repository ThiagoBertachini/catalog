package com.tbemerencio.catalog.repositories;

import com.tbemerencio.Factory;
import com.tbemerencio.catalog.controllers.dtos.ProductDTO;
import com.tbemerencio.catalog.entities.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Optional;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    private Long validId;
    private Long notExistingId;

    @BeforeEach
    void setUp(){
        validId = 1L;
        notExistingId = 1000L;
    }

    @Test
    void deleteShouldDeleteWhenValidId(){
        productRepository.deleteById(validId);

        Optional<Product> entityOpt = productRepository.findById(validId);

        Assertions.assertTrue(entityOpt.isEmpty());
    }

    @Test
    void deleteShouldThrowExceptionWhenInvalidId(){
        Assertions.assertThrows(EmptyResultDataAccessException.class, () -> productRepository.deleteById(notExistingId));
    }

    @Test
    void saveShouldSabeNewEntityWhenNullId(){
        Product productToSave = Factory.createProduct();
        productToSave.setId(null);

        Product entity = productRepository.save(productToSave);

        Assertions.assertNotNull(entity.getId());
    }

    @Test
    void findByIdShouldReturnOptionalProjectPresentWhenValidId(){
        Optional<Product> product = productRepository.findById(validId);

        Assertions.assertTrue(product.isPresent());
    }

    @Test
    void findByIdShouldReturnOptionalProjectEmptyWhenValidId(){
        Optional<Product> product = productRepository.findById(notExistingId);

        Assertions.assertTrue(product.isEmpty());
    }
}