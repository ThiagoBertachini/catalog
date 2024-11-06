package com.tbemerencio.catalog.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbemerencio.Factory;
import com.tbemerencio.catalog.controllers.dtos.ProductDTO;
import com.tbemerencio.catalog.services.ProductService;
import com.tbemerencio.catalog.services.exceptions.DataBaseIntegrityException;
import com.tbemerencio.catalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductDTO productDTO;
    private final Long validId = 1L;
    private final Long invalidID = 2L;
    private final Long associatedID = 3L;
    private final String uri = "/api/products";

    @BeforeEach
    void setUp() {
        productDTO = Factory.createProductDTO();
        PageImpl<ProductDTO> productDTOPage = new PageImpl<>(List.of(productDTO));

        when(productService.findAllPaged((Pageable) any())).thenReturn(productDTOPage);
        when(productService.findByID(validId)).thenReturn(ResponseEntity.ok(productDTO).getBody());
        when(productService.findByID(invalidID)).thenThrow(ResourceNotFoundException.class);
        when(productService.update(eq(validId), any())).thenReturn(ResponseEntity.ok(productDTO).getBody());
        when(productService.update(eq(invalidID), any())).thenThrow(ResourceNotFoundException.class);
        doNothing().when(productService).delete(validId);
        doThrow(ResourceNotFoundException.class).when(productService).delete(invalidID);
        doThrow(DataBaseIntegrityException.class).when(productService).delete(associatedID);
        when(productService.create(any())).thenReturn(productDTO);

    }

    @Test
    void findAllPagedShouldReturnProductDTOPaged() throws Exception {
        //ResultActions resultActions =
        //        mockMvc.perform(get("/api/products"));
        //resultActions.andExpect(status().isOk());

        mockMvc.perform(
                get(uri).accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk());
    }

    @Test
    void findByIdShouldReturnProductDTOWhenValidId() throws Exception {
        mockMvc.perform(
                get(uri + "/{id}", validId).accept(MediaType.APPLICATION_JSON)
        ).andExpect(jsonPath("$.id", "$.name", "$.description").exists())
                .andExpect(status().isOk());
    }

    @Test
    void findByIdShouldReturnNotFoundWhenInvalidId() throws Exception {
        mockMvc.perform(
                get(uri + "/{id}", invalidID).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    void updateShouldReturnSuccessWhenValidId() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        mockMvc.perform(
                put(uri + "/update/{id}", validId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(status().isOk());
    }

    @Test
    void createShouldReturnOkWhenValidBody() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        mockMvc.perform(
                post(uri)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void updateShouldReturnNotFoundWhenInvalidId() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        mockMvc.perform(
                        put(uri + "/update/{id}", invalidID)
                                .content(jsonBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteShoudDoNothingWhenValidId() throws Exception {
        mockMvc.perform(
                        delete(uri + "/delete/{id}", validId)
                                .contentType(MediaType.APPLICATION_JSON)
                                ).andExpect(status().is(HttpStatus.NO_CONTENT.value()));
    }

    @Test
    void deleteShoudReturn404WhenInvlidId() throws Exception {
        mockMvc.perform(
                delete(uri + "/delete/{id}", invalidID)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    void deleteShoudReturn503WhenAssociatedId() throws Exception {
        mockMvc.perform(
                delete(uri + "/delete/{id}", associatedID)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError());
    }
}