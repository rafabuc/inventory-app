package com.rbm.inventory.service;

import com.rbm.inventory.message.ProductMessage;
import com.rbm.inventory.model.Inventory;
import com.rbm.inventory.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Implementación por defecto de {@link InventoryService}.
 * <p>
 * {@inheritDoc}
 */
@Service
public class InventoryServiceImpl implements InventoryService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);

    private final InventoryRepository inventoryRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }


    public void proccessProductMessage(ProductMessage message) {

        inventoryRepository.findOptionalByProductId(message.getProductId())
                .map(inventory -> {
                    logger.error("Actualizando stock : ", inventory);
                    inventory.setName(message.getName());
                    inventory.setStock(message.getStock());
                    inventoryRepository.save(inventory);
                    return inventory;
                }).orElseGet(() -> {
                    Inventory model = new Inventory();
                    model.setName(message.getName());
                    model.setProductId(message.getProductId());
                    model.setStock(message.getStock());
                    logger.error("Nuevo inventario stock : ", model);
                    inventoryRepository.save(model);
                    return model;
                });
    }
}
