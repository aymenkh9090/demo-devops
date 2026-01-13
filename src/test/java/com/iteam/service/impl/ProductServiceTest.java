package com.iteam.service.impl;

import com.iteam.Exceptions.NotFoundEntityExceptions;
import com.iteam.entities.Product;
import com.iteam.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test unitaire de Produit service")
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;
    @InjectMocks
    ProductServiceImpl productService;

    Product product;
    Product product2;



    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setNameProduct("Samsung");
        product.setPrice(999.0);
        product.setQuantity(4);

        product2 = new Product();
        product2.setId(2L);
        product2.setNameProduct("Laptop");
        product2.setPrice(9999.0);
        product2.setQuantity(2);

    }

    @Test
    @DisplayName("Doit retourner une liste de produit")
    void findAll_ShouldRetourneAllProduct() {
        //Arrange
        List<Product> products = Arrays.asList(
                product,product2

        );

        when(productRepository.findAll()).thenReturn(products);
        //Act
        List<Product> result = productService.findAll();
        //Assert
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getNameProduct()).isEqualTo("Samsung");
        assertThat(result.get(1).getNameProduct()).isEqualTo("Laptop");


    }

    @Test
    @DisplayName("Doit cree un produit avec succes")
    void createProduct_ShouldSaveProduct() {
        //Arrange
        when(productRepository.save(any(Product.class))).thenReturn(product);
        //Act
        Product result = productService.createProduct(product);
        //Assert
        assertThat(result.getNameProduct()).isEqualTo("Samsung");
        assertThat(result.getPrice()).isEqualTo(999.0);
    }

    @Test
    @DisplayName("Doit trouver un produit avec son id")
    void findProductById_ShouldReturnProduct_WhenProductIdIsFound() {
        //Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        //Act
        Product result = productService.findProductById(1L);
        //Assert
        assertThat(result.getNameProduct()).isEqualTo("Samsung");
        assertThat(result.getPrice()).isEqualTo(999.0);
    }
    @Test
    @DisplayName("Doit retourner une exception quand produit n'existe pas")
    void findProductById_ShouldThrowException_WhenProductNotFound() {
        //Arrange
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        //Act&&Assert
        assertThatThrownBy(() -> productService.findProductById(99L))
                .isInstanceOf(NotFoundEntityExceptions.class)
                .hasMessage("No Product present with ID : 99");

    }

    @Test
    @DisplayName("updateProduct() - Doit mettre à jour un produit existant")
    void updateProduct_ShouldUpdateProduct_WhenProductExists() {
        // Arrange
        Product updatedProduct = new Product();
        updatedProduct.setNameProduct("Laptop Pro");
        updatedProduct.setPrice(2000.0);
        updatedProduct.setQuantity(15);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // Act
        Product result = productService.updateProduct(1L, updatedProduct);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getNameProduct()).isEqualTo("Laptop Pro");
        assertThat(result.getPrice()).isEqualTo(2000.0);
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("deleteProduct() - Doit supprimer un produit existant")
    void deleteProduct_ShouldDeleteProduct_WhenProductExists() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(any(Product.class));

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).delete(product);
    }
}