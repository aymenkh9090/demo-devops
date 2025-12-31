package com.iteam.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Product extends BaseEntity {


    @Column(nullable = false)
    private String nameProduct;
    @Column(nullable = false)
    private Double price;
    @Column(nullable = false)
    private Integer quantity;





}
