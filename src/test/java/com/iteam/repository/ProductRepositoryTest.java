package com.iteam.repository;

import com.iteam.entities.Product;
import com.iteam.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Sql(scripts = "/schema.sql")
@DisplayName("Tests du repository Product")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    private Product product;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        product = new Product();
        product.setNameProduct("Laptop");
        product.setPrice(1500.0);
        product.setQuantity(10);
    }

    @Test
    @DisplayName("Doit sauvegarder et retrouver un produit")
    void saveAndFind_ShouldWork() {

        Product saved = productRepository.save(product);


        Optional<Product> found = productRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getNameProduct()).isEqualTo("Laptop");
        assertThat(found.get().getPrice()).isEqualTo(1500.0);
    }

    @Test
    @DisplayName("Doit retourner tous les produits")
    void findAll_ShouldReturnAllProducts() {

        productRepository.save(new Product( "Laptop", 1500.0, 10));
        productRepository.save(new Product( "Phone", 800.0, 20));


        List<Product> products = productRepository.findAll();


        assertThat(products).hasSize(2);
        assertThat(products.get(0).getNameProduct()).isEqualTo("Laptop");
        assertThat(products.get(1).getNameProduct()).isEqualTo("Phone");
    }

    @Test
    @DisplayName("Doit mettre à jour un produit")
    void updateProduct_ShouldWork() {

        Product saved = productRepository.save(product);
        saved.setNameProduct("Laptop Updated");
        saved.setPrice(2000.0);


        productRepository.save(saved);
        Optional<Product> updated = productRepository.findById(saved.getId());


        assertThat(updated).isPresent();
        assertThat(updated.get().getNameProduct()).isEqualTo("Laptop Updated");
        assertThat(updated.get().getPrice()).isEqualTo(2000.0);
    }

    @Test
    @DisplayName("Doit supprimer un produit")
    void deleteProduct_ShouldWork() {

        Product saved = productRepository.save(product);
        Long productId = saved.getId();


        productRepository.deleteById(productId);
        Optional<Product> found = productRepository.findById(productId);


        assertThat(found).isEmpty();
    }
}