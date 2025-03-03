package com.tbemerencio.catalog.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbemerencio.Factory;
import com.tbemerencio.catalog.TokenUtil;
import com.tbemerencio.catalog.controllers.dtos.ProductDTO;
import com.tbemerencio.catalog.services.ProductService;
import com.tbemerencio.catalog.services.exceptions.DataBaseIntegrityException;
import com.tbemerencio.catalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;
    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductDTO productDTO;
    private final Long validId = 1L;
    private final Long invalidID = 2L;
    private final Long associatedID = 3L;
    private final String uri = "/api/products";
    private String userName = "maria@gmail.com";
    private String password = "123456";

    @BeforeEach
    void setUp() {
        productDTO = Factory.createProductDTO();
        PageImpl<ProductDTO> productDTOPage = new PageImpl<>(List.of(productDTO));

        when(productService.findAllPaged(any(), any(), any())).thenReturn(productDTOPage);
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
                        .header("Authorization", "Bearer " + getToken())
                ).andExpect(status().isOk());
    }

    @Test
    void findByIdShouldReturnProductDTOWhenValidId() throws Exception {
        mockMvc.perform(
                get(uri + "/{id}", validId).accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + getToken())
                ).andExpect(jsonPath("$.id", "$.name", "$.description").exists())
                .andExpect(status().isOk());
    }

    @Test
    void findByIdShouldReturnNotFoundWhenInvalidId() throws Exception {
        mockMvc.perform(
                get(uri + "/{id}", invalidID).accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + getToken())
        ).andExpect(status().isNotFound());
    }

    @Test
    void updateShouldReturnSuccessWhenValidId() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        mockMvc.perform(
                put(uri + "/update/{id}", validId)
                        .header("Authorization", "Bearer " + getToken())
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
                        .header("Authorization", "Bearer " + getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void updateShouldReturnNotFoundWhenInvalidId() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        mockMvc.perform(
                        put(uri + "/update/{id}", invalidID)
                                .header("Authorization", "Bearer " + getToken())
                                .content(jsonBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteShoudDoNothingWhenValidId() throws Exception {
        mockMvc.perform(
                        delete(uri + "/delete/{id}", validId)
                                .header("Authorization", "Bearer " + getToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                ).andExpect(status().is(HttpStatus.NO_CONTENT.value()));
    }

    @Test
    void deleteShoudReturn404WhenInvlidId() throws Exception {
        mockMvc.perform(
                delete(uri + "/delete/{id}", invalidID)
                        .header("Authorization", "Bearer " + getToken())
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

    private String getToken() throws Exception {
        return tokenUtil.obtainAccessToken(mockMvc, userName, password);
    }
}