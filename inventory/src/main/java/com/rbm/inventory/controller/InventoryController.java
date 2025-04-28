package com.rbm.inventory.controller;

import com.rbm.inventory.model.Inventory;
import com.rbm.inventory.repository.InventoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para la entidad @see {@link Inventory}
 * Expone los siguientes metodos:
 * <ul>
 *     <li>Obtener la lista de inventario  de todos los productos</li>
 *     <li>Buscar stock por id de producto</li>
 *     <li>Crear nuevo stock producto</li>
 *     <li>Editar stock producto</li>
 *     <li>Borrar stock de producto por id</li>
 * </ul>
 */
@RestController
public class InventoryController {


    private final InventoryRepository inventoryRepository;

    public InventoryController(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    /**
     * Devuelve una lista de todos los inventarios de producto
     *
     * @return @{@link ResponseEntity} con la lista de productos
     */
    @GetMapping("/inventory")
    public ResponseEntity<List<Inventory>> listAll() {
        return ResponseEntity.ok(inventoryRepository.findAll());
    }

    /**
     * Devuelve un {@link Inventory} por ID
     *
     * @param id el identificador del producto
     * @return @{@link ResponseEntity} con el inventario encontrado, en caso de no
     * encontrarlo devuelve estado de not found
     */
    @GetMapping("/inventory/products/{id}")
    public ResponseEntity<Inventory> findByProductId(@PathVariable Long id) {
        Optional<Inventory> product = inventoryRepository.findOptionalByProductId(id);
        return product.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Crea un {@link Inventory} para el id_product recibido, si ya existe suma el stock de dicho producto
     *
     * @param newInventory el {@link Inventory} que se crea
     * @return {@link ResponseEntity} con la ubicacion del inventario creado
     */
    @PostMapping("/inventory/products")
    public ResponseEntity<Inventory> createInventory(@RequestBody Inventory newInventory) {

        Optional<Inventory> inventory = inventoryRepository.findOptionalByProductId(newInventory.getProductId());

        return inventory.map( inventoryFound-> {
            inventoryFound.setStock(inventoryFound.getStock() + newInventory.getStock() );
            inventoryRepository.save(inventoryFound);
            return ResponseEntity.ok(inventoryFound);
        } ).orElseGet(
                ()->{
                   inventoryRepository.save(newInventory);
                   return ResponseEntity.ok(newInventory);
                }
        );
    }

    /**
     * Actualiza unn {@link Inventory}, actualiza el stock de un producto al stock recibido
     *
     * @param inventoryToUpdate inventario de producto que se actualizara
     * @param id              identificador del producto qyue se actualiza
     * @return {@link ResponseEntity} con el inventario actualizado , en caso de no encontrarlo se devuelve estado de no encontrado
     */
    @PutMapping("/inventory/products/{id}")
    public ResponseEntity<?> updateInventory(@RequestBody Inventory inventoryToUpdate, @PathVariable Long id) {

        return ResponseEntity.ok(inventoryRepository.findOptionalByProductId(id)
                .map(inventory -> {
                    inventory.setName(inventoryToUpdate.getName());
                    inventory.setStock(inventoryToUpdate.getStock());
                    inventoryRepository.save(inventory);
                    return ResponseEntity.ok(inventory);
                }).orElseGet(() -> {
                    return ResponseEntity.noContent().build();
                }));

    }

    /**
     * Elimina un  {@link Inventory} por id del producto
     *
     * @param id
     * @return
     */
    @DeleteMapping("/inventory/products/{id}")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
        inventoryRepository.deleteByProductId(id);
        return ResponseEntity.ok().build();
    }

}
