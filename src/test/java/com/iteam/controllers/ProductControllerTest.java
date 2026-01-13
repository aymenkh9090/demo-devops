package com.iteam.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iteam.entities.Product;
import com.iteam.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;



@WebMvcTest(ProductController.class)
@DisplayName("Test de ProductController")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ProductService productService;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

    }

    @Test
    @DisplayName("POST /api/products/create - Create Product")
    void createProduct_Success() throws Exception {

        Product product = new Product();
        product.setId(1L);
        product.setNameProduct("Phone");
        product.setPrice(1200.0);
        product.setQuantity(3);

        when(productService.createProduct(any(Product.class)))
                .thenReturn(product);

        mockMvc.perform(post("/api/products/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Created Product Successfully"))
                .andExpect(jsonPath("$.Product.id").value(1))
                .andExpect(jsonPath("$.Product.nameProduct").value("Phone"));
    }



    @Test
    @DisplayName("GET /api/products - Get all products")
    void getAllProducts_Success() throws Exception {

        Product p1 = new Product("Samsung", 1200.0, 5);
        Product p2 = new Product("Casque", 78.2, 30);

        when(productService.findAll()).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].nameProduct").value("Samsung"));
    }


    @Test
    @DisplayName("GET /api/products/{id} - Get product by id")
    void getProductById_Success() throws Exception {

        Product product = new Product("Samsung", 1200.0, 5);

        when(productService.findProductById(1L)).thenReturn(product);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                //.andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nameProduct").value("Samsung"));
    }


    @Test
    @DisplayName("PUT /api/products/{id} - Update product")
    void updateProduct_Success() throws Exception {

        Product updatedProduct = new Product("Phone Pro", 1500.0, 4);

        when(productService.updateProduct(eq(1L), any(Product.class)))
                .thenReturn(updatedProduct);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nameProduct").value("Phone Pro"))
                .andExpect(jsonPath("$.price").value(1500));
    }


    @Test
    @DisplayName("DELETE /api/products/{id} - Delete product")
    void deleteProduct_Success() throws Exception {

        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product delet with succes"))
                .andExpect(jsonPath("$.ID").value(1));
    }
}

