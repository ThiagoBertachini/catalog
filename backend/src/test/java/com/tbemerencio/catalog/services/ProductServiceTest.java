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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @InjectMocks
    private ProductService productService;

    private Long validId;
    private Long notValidId;
    private Long dependentId;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        validId = 1L;
        notValidId = 2L;
        dependentId = 3L;
        Product product = Factory.createProduct();
        PageImpl<Product> pageProduct = new PageImpl<>(List.of(product));
        productDTO = Factory.createProductDTO();

        doNothing().when(productRepository).deleteById(validId);
        doThrow(ResourceNotFoundException.class).when(productRepository).deleteById(notValidId);
        doThrow(DataIntegrityViolationException.class).when(productRepository).deleteById(dependentId);
        when(productRepository.findAll((Pageable) any())).thenReturn(pageProduct);
        when(productRepository.save(any())).thenReturn(product);
        when(productRepository.findById(validId)).thenReturn(Optional.of(product));
        when(productRepository.findById(notValidId)).thenReturn(Optional.empty());

        when(productRepository.findProduct(any(), any(), any())).thenReturn(pageProduct);
    }

    @Test
    void deleteShouldDeleteWhenValidId() {
        assertDoesNotThrow(() -> productService.delete(validId));
        verify(productRepository, times(1)).deleteById(validId);
    }

    @Test
    void deleteShouldThrowExceptionWhenNotValidId() {
        assertThrows(ResourceNotFoundException.class,
                () -> productService.delete(notValidId));
        verify(productRepository, times(1)).deleteById(notValidId);
    }

    @Test
    void deleteShouldThrowExceptionWhenDependentId() {
        assertThrows(DataBaseIntegrityException.class,
                () -> productService.delete(dependentId));
        verify(productRepository, times(1)).deleteById(dependentId);
    }

    @Test
    void saveShouldSaveProductWithCategoryWhenValidObject() {
        when(categoryRepository.findById(any())).thenReturn(Optional.of(new Category(1L, "new category")));

        ProductDTO productSaved = assertDoesNotThrow(() -> productService.create(Factory.createProductDTO()));

        assertNotNull(productSaved);
        assertNotNull(productSaved.getCategories());
        verify(productRepository, times(1)).save(any());
        verify(categoryRepository, times(1)).findById(any());
    }

    @Test
    void saveShouldSaveProductWithOutCategoryWhenValidObject() {
        productDTO.getCategories().clear();

        ProductDTO productSaved = assertDoesNotThrow(() -> productService.create(productDTO));

        assertNotNull(productSaved);
        assertTrue(productSaved.getCategories().isEmpty());
        verify(productRepository, times(1)).save(any());
        verify(categoryRepository, times(0)).findById(any());
    }

    @Test
    void findAllShoudReturPageOfProduct(){
        Page<ProductDTO> productPage = productService.findAllPaged(0L, "test", Pageable.ofSize(1));

        assertFalse(productPage.isEmpty());
    }

    @Test
    void findByIdShouldReturnProductWhenValidId() {
        ProductDTO productSaved = assertDoesNotThrow(() -> productService.findByID(validId));

        assertNotNull(productSaved);
        verify(productRepository, times(1)).findById(validId);
    }

    @Test
    void findByIdShouldThrowExceptionWhenNotValidId() {
        ResourceNotFoundException err = assertThrows(ResourceNotFoundException.class,
                () -> productService.findByID(notValidId));

        assertEquals("ID not found [" + notValidId + "]", err.getMessage());
        verify(productRepository, times(1)).findById(notValidId);
    }
}