package com.rbm.product.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class Product {
    @Id
    @GeneratedValue
    private Long id;

    private String description;
    private String name;
    private String code;
    private String weight;

    public Product() {
    }

    public Product(String description, String name, String code, String weight) {
        this.description = description;
        this.name = name;
        this.code = code;
        this.weight = weight;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id.equals(product.id) && description.equals(product.description) && name.equals(product.name) && code.equals(product.code) && weight.equals(product.weight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, name, code, weight);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", weight='" + weight + '\'' +
                '}';
    }


}
