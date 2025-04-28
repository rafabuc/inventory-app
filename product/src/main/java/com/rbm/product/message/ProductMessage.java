package com.rbm.product.message;


import java.io.Serializable;

public class ProductMessage implements Serializable {
    private Long productId;
    private String name;
    private Integer stock;

    // Default constructor for JSON deserialization
    public ProductMessage() {
    }

    public ProductMessage(Long productId, String name, Integer stock) {
        this.productId = productId;
        this.name = name;
        this.stock = stock;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
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

    @Override
    public String toString() {
        return "ProductMessage{" +
                "productId=" + productId +
                ", name='" + name + '\'' +
                ", stock=" + stock +
                '}';
    }
}