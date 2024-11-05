package com.tbemerencio.catalog.controllers;

import com.tbemerencio.Factory;
import com.tbemerencio.catalog.controllers.dtos.ProductDTO;
import com.tbemerencio.catalog.services.ProductService;
import com.tbemerencio.catalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    private ProductDTO productDTO;
    private PageImpl<ProductDTO> productDTOPage;
    private Long validId = 1L;
    private Long invalidID = 2L;
    @BeforeEach
    void setUp() {
        productDTO = Factory.createProductDTO();
        productDTOPage = new PageImpl<>(List.of(productDTO));

        when(productService.findAllPaged((Pageable) any())).thenReturn(productDTOPage);
        when(productService.findByID(validId)).thenReturn(ResponseEntity.ok(productDTO).getBody());
        when(productService.findByID(invalidID)).thenThrow(ResourceNotFoundException.class);

    }

    @Test
    void findAllPagedShouldReturnProductDTOPaged() throws Exception {
        //ResultActions resultActions =
        //        mockMvc.perform(get("/api/products"));
        //resultActions.andExpect(status().isOk());

        mockMvc.perform(
                get("/api/products").accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk());
    }

    @Test
    void findByIdShouldReturnProductDTOWhenValidId() throws Exception {
        mockMvc.perform(
                get("/api/products/{id}", validId).accept(MediaType.APPLICATION_JSON)
        ).andExpect(jsonPath("$.id", "$.name", "$.description").exists())
                .andExpect(status().isOk());
    }

    @Test
    void findByIdShouldReturnNotFoundWhenInvalidId() throws Exception {
        mockMvc.perform(
                get("/api/products/{id}", invalidID).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }
}