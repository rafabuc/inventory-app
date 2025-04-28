package com.rbm.inventory.repository;

import com.rbm.inventory.model.Inventory;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad {@link Inventory}
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    /**
     *
     * @param productId
     * @return
     */
    Optional<Inventory> findOptionalByProductId(Long productId);

    /**
     *
     * @param productId
     * @return
     */
    Inventory findByProductId(Long productId);


    /**
     * Método para eliminar por productId
     * @param productId
     */
    @Transactional
    long deleteByProductId(Long productId);

    //List<Inventory> findAllByProductId(Long productId);

}
