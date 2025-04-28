package com.rbm.inventory.service;


import com.rbm.inventory.message.ProductMessage;

/**
 * Interface de servicio para procesar mensajes
 */
public interface InventoryService {

    /**
     * Procesa el message recibido del listener {@link ProductSqsConsumer}
     * El mensaje es para crear stock en el inventario para un nuevo producto
     * o para actualizarlo
     * Si el producto existe actualiza su stock en el inventario
     * Si no existe crea el inventario con el stock recibido
     *
     * @param message mensaje recibido del listener
     */
    public void proccessProductMessage(ProductMessage message);

}
