package com.rbm.inventory.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.util.Objects;

/**
 * Entidad de base datos
 */
@Entity
public class Inventory {
    @Id
    @GeneratedValue
    private Long id;
    private String name;

    private Long productId;

    private Integer stock;

    public Inventory() {
    }
    public Inventory( String name, Long productId, Integer stock) {
        this.name = name;
        this.productId = productId;
        this.stock = stock;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", productId=" + productId +
                ", stock=" + stock +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Inventory inventory)) return false;
        return Objects.equals(getId(), inventory.getId()) && Objects.equals(getName(), inventory.getName()) && Objects.equals(getProductId(), inventory.getProductId()) && Objects.equals(getStock(), inventory.getStock());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getProductId(), getStock());
    }
}
