package com.rbm.product;

import com.rbm.product.controller.ProductController;
import com.rbm.product.message.ProductMessage;
import com.rbm.product.model.Product;
import com.rbm.product.repository.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProductControllerTest {

    @Mock
    private ProductRepository productRepository;



    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listAll_ShouldReturnAllProducts() {
        // Arrange
        List<Product> expectedProducts = Arrays.asList(
                createProduct(1L, "P001", "Producto 1", "Descripción 1", "10.5"),
                createProduct(2L, "P002", "Producto 2", "Descripción 2", "20.0")
        );
        when(productRepository.findAll()).thenReturn(expectedProducts);

        // Act
        ResponseEntity<List<Product>> response = productController.listAll();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedProducts, response.getBody());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void findById_WithExistingId_ShouldReturnProduct() {
        // Arrange
        Long productId = 1L;
        Product expectedProduct = createProduct(productId, "P001", "Producto 1", "Descripción 1", "10.5");
        when(productRepository.findById(productId)).thenReturn(Optional.of(expectedProduct));

        // Act
        ResponseEntity<Product> response = productController.findById(productId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedProduct, response.getBody());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void findById_WithNonExistingId_ShouldReturnNotFound() {
        // Arrange
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Product> response = productController.findById(productId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void createProduct_ShouldSaveAndReturnCreatedLocation() {
        // Arrange
        Product newProduct = createProduct(null, "P003", "Producto Nuevo", "Descripción Nueva", "15.0");
        Product savedProduct = createProduct(3L, "P003", "Producto Nuevo", "Descripción Nueva", "15.0");

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost");

        // Act
        ResponseEntity<Product> response = productController.createProduct(newProduct, uriBuilder);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getHeaders().getLocation());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void updateProduct_WithExistingId_ShouldUpdateAndReturnProduct() {
        // Arrange
        Long productId = 1L;
        Product existingProduct = createProduct(productId, "P001", "Producto 1", "Descripción 1", "10.5");
        Product updatedProductData = createProduct(productId, "P001-Updated", "Producto Actualizado", "Descripción Actualizada", "15.0");
        Product expectedUpdatedProduct = createProduct(productId, "P001-Updated", "Producto Actualizado", "Descripción Actualizada", "15.0");

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(expectedUpdatedProduct);

        // Act
        ResponseEntity<?> response = productController.updateProduct(updatedProductData, productId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(any(Product.class));
    }


    @Test
    void deleteProduct_ShouldDeleteAndReturnOk() {
        // Arrange
        Long productId = 1L;
        doNothing().when(productRepository).deleteById(productId);

        // Act
        ResponseEntity<Void> response = productController.deleteProduct(productId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productRepository, times(1)).deleteById(productId);
    }



    private Product createProduct(Long id, String code, String name, String description, String weight) {
        Product product = new Product();
        product.setId(id);
        product.setCode(code);
        product.setName(name);
        product.setDescription(description);
        product.setWeight(weight);
        return product;
    }

}

    /*

        @Mock
    private ProductSqsProducer productSqsProducer;

    @Test
    void sendProductMessage_ShouldSendMessageAndReturnConfirmation() {
        // Arrange
        ProductMessage productMessage = new ProductMessage();
        productMessage.setProductId(1L);
        productMessage.setName("Producto Mensaje");
        doNothing().when(productSqsProducer).sendProductMessage(any(ProductMessage.class));

        // Act
        String response = productController.sendProductMessage(productMessage);

        // Assert
        assertEquals("Mensaje enviado correctamente a SQS", response);
        verify(productSqsProducer, times(1)).sendProductMessage(any(ProductMessage.class));
    }
    */