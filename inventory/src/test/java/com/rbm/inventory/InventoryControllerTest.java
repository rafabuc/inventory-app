package com.rbm.inventory;


import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.rbm.inventory.controller.InventoryController;
import com.rbm.inventory.model.Inventory;
import com.rbm.inventory.repository.InventoryRepository;
import org.json.JSONArray;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class InventoryControllerTest {

    @Autowired
    TestRestTemplate restTemplate;


    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryController inventoryController;

    private Inventory inventory1;
    private Inventory inventory2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        inventory1 = new Inventory();
        inventory1.setProductId(1L);
        inventory1.setName("Producto 1");
        inventory1.setStock(10);

        inventory2 = new Inventory();
        inventory2.setProductId(2L);
        inventory2.setName("Producto 2");
        inventory2.setStock(20);
    }

    @Test
    void listAll_ShouldReturnAllInventories() {
        // Arrange
        List<Inventory> expectedInventories = Arrays.asList(inventory1, inventory2);
        when(inventoryRepository.findAll()).thenReturn(expectedInventories);

        // Act
        ResponseEntity<List<Inventory>> response = inventoryController.listAll();

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(expectedInventories, response.getBody());
        verify(inventoryRepository, times(1)).findAll();
    }

    @Test
    void findByProductId_WhenProductExists_ShouldReturnInventory() {
        // Arrange
        when(inventoryRepository.findOptionalByProductId(1L)).thenReturn(Optional.of(inventory1));

        // Act
        ResponseEntity<Inventory> response = inventoryController.findByProductId(1L);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(inventory1, response.getBody());
        verify(inventoryRepository, times(1)).findOptionalByProductId(1L);
    }



    @Test
    void findByProductId_WhenProductNotExists_ShouldReturnNotFound() {
        // Arrange
        when(inventoryRepository.findOptionalByProductId(99L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Inventory> response = inventoryController.findByProductId(99L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(inventoryRepository, times(1)).findOptionalByProductId(99L);
    }

    @Test
    void createInventory_WhenProductNotExists_ShouldCreateNewInventory() {
        // Arrange
        Inventory newInventory = new Inventory();
        newInventory.setProductId(3L);
        newInventory.setName("Producto 3");
        newInventory.setStock(30);

        when(inventoryRepository.findOptionalByProductId(3L)).thenReturn(Optional.empty());
        when(inventoryRepository.save(newInventory)).thenReturn(newInventory);

        // Act
        ResponseEntity<Inventory> response = inventoryController.createInventory(newInventory);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(newInventory, response.getBody());
        verify(inventoryRepository, times(1)).findOptionalByProductId(3L);
        verify(inventoryRepository, times(1)).save(newInventory);
    }

    @Test
    void createInventory_WhenProductExists_ShouldUpdateStock() {
        // Arrange
        Inventory existingInventory = new Inventory();
        existingInventory.setProductId(1L);
        existingInventory.setName("Producto 1");
        existingInventory.setStock(10);

        Inventory additionalInventory = new Inventory();
        additionalInventory.setProductId(1L);
        additionalInventory.setStock(5);

        Inventory expectedInventory = new Inventory();
        expectedInventory.setProductId(1L);
        expectedInventory.setName("Producto 1");
        expectedInventory.setStock(15);

        when(inventoryRepository.findOptionalByProductId(1L)).thenReturn(Optional.of(existingInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(expectedInventory);

        // Act
        ResponseEntity<Inventory> response = inventoryController.createInventory(additionalInventory);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(15, response.getBody().getStock());
        verify(inventoryRepository, times(1)).findOptionalByProductId(1L);
        verify(inventoryRepository, times(1)).save(existingInventory);
    }

    @Test
    void deleteInventory_ShouldCallRepositoryDelete() {
        // Arrange
        //doNothing().when(inventoryRepository).deleteByProductId(1L);

        // Act
        ResponseEntity<Void> response = inventoryController.deleteInventory(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(inventoryRepository, times(1)).deleteByProductId(1L);
    }
}