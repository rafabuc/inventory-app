package com.rbm.inventory.message;


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

    public static ProductMessage fromString(String input) {
        ProductMessage message = new ProductMessage();

        // Remove the class name and outer braces
        String content = input.replace("ProductMessage{", "").replace("}", "");

        // Split by commas, but be careful with commas within quotes
        String[] parts = content.split(",(?=([^']*'[^']*')*[^']*$)");

        for (String part : parts) {
            String[] keyValue = part.trim().split("=", 2);
            String key = keyValue[0].trim();
            String value = keyValue[1].trim();

            switch (key) {
                case "productId":
                    message.setProductId(Long.parseLong(value));
                    break;
                case "name":
                    // Remove surrounding quotes
                    message.setName(value.substring(1, value.length() - 1));
                    break;
                case "stock":
                    message.setStock(Integer.parseInt(value));
                    break;
            }
        }

        return message;
    }
}