package com.tbemerencio.catalog.services;

import com.tbemerencio.Factory;
import com.tbemerencio.catalog.controllers.dtos.ProductDTO;
import com.tbemerencio.catalog.entities.Category;
import com.tbemerencio.catalog.entities.Product;
import com.tbemerencio.catalog.repositories.CategoryRepository;
import com.tbemerencio.catalog.repositories.ProductRepository;
import com.tbemerencio.catalog.services.exceptions.DataBaseIntegrityException;
import com.tbemerencio.catalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class ProductServiceIntegratedTest {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;

    private Long validId;
    private Long countTotalProducts;

    @BeforeEach
    void setUp() {
        validId = 1L;
        countTotalProducts = 25L;

        Product product = Factory.createProduct();
        PageImpl<Product> pageProduct = new PageImpl<>(List.of(product));
        ProductDTO productDTO = Factory.createProductDTO();
    }

    @Test
    void deleteShouldDeleteWhenValidId() {
        assertDoesNotThrow(() -> productService.delete(validId));
        assertThrows(ResourceNotFoundException.class, () -> productService.delete(validId));
        assertEquals(countTotalProducts - 1, productRepository.count());
    }

    @Test
    void findAllShouldReturnPagedProduct() {
        Page<ProductDTO> productPaged = productService.findAllPaged(PageRequest.of(0, 5));

        assertFalse(productPaged.isEmpty());
        assertEquals(0, productPaged.getNumber());
        assertEquals(5, productPaged.getSize());
        assertEquals(countTotalProducts, productPaged.getTotalElements());
    }

    @Test
    void findAllShouldReturnEmptyPageWhenPageDoesNotExists() {
        Page<ProductDTO> productPaged = productService.findAllPaged(PageRequest.of(55, 5));

        assertTrue(productPaged.isEmpty());
        assertEquals(countTotalProducts, productPaged.getTotalElements());
    }

    @Test
    void findAllShouldReturnOrderedPageProductWhenOrderByName() {
        Page<ProductDTO> productPaged = productService.findAllPaged(PageRequest.of(0, 5, Sort.by("name")));

        assertFalse(productPaged.isEmpty());
        assertEquals("Macbook Pro", productPaged.getContent().get(0).getName());
    }
}