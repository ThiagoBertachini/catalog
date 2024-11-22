package com.tbemerencio.catalog.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbemerencio.Factory;
import com.tbemerencio.catalog.controllers.dtos.ProductDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductControllerIntegratedTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductDTO productDTO;
    private final String uri = "/api/products";
    private final String paged = "?page=0&size=12&sort=name,asc";

    @BeforeEach
    void setUp() {
        productDTO = Factory.createProductDTO();
        PageImpl<ProductDTO> productDTOPage = new PageImpl<>(List.of(productDTO));
    }

    @Test
    void findAllShouldReturnOrderedPagedByName() throws Exception {
        //        mockMvc.perform(get("/api/products"));
        //resultActions.andExpect(status().isOk());

        ResultActions result = mockMvc.perform(
                get(uri + paged).accept(MediaType.APPLICATION_JSON));

        Long totalElements = 25L;
        result.andExpect(jsonPath("$.totalElements").value(totalElements));
       result.andExpect(jsonPath("$.content").exists());
       result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
    }

    @Test
    void updateShouldReturnSuccessWhenValidId() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        Long validId = 1L;
        mockMvc.perform(
                put(uri + "/update/{id}", validId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(productDTO.getName()))
                .andExpect(status().isOk());
    }
}