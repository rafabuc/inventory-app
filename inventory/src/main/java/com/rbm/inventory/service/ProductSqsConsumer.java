package com.rbm.inventory.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbm.inventory.message.ProductMessage;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Servicio consumer de mensajeria SQS
 */
@Service
public class ProductSqsConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ProductSqsConsumer.class);
    private final ObjectMapper objectMapper;
    private final InventoryService inventoryService;

    public ProductSqsConsumer(ObjectMapper objectMapper, InventoryService inventoryService) {
        this.objectMapper = objectMapper;
        this.inventoryService = inventoryService;
    }

    /**
     * Listener a SQS
     * Recibe el mensaje como String. el cual lee y lo pasa al servicio
     * {@link InventoryService} para ser procesado
     *
     * @param rawMessage
     */
    @SqsListener("${aws.sqs.product-queue}")
    public void receiveRawMessage(String rawMessage) {
        logger.info("Mensaje raw recibido: {}", rawMessage);
        try {
            // deserializar manualmente
            ProductMessage message = ProductMessage.fromString(rawMessage);
            //ProductMessage message =objectMapper.readValue(rawMessage, ProductMessage.class);
            System.out.println("Mensaje deserializado: " + message);

            this.inventoryService.proccessProductMessage(message);
        } catch (Exception e) {
            logger.error("Error al procesar mensaje raw: ", e);
        }
    }


}


/*
    @SqsListener("${aws.sqs.product-queue}")
    public void receiveProductMessage(ProductMessage productMessage) {
        logger.info("Intentando procesar mensaje de SQS");
        try {
            logger.info("Mensaje recibido de la cola SQS: {}", productMessage);

            // Aquí puedes hacer lo que necesites con el mensaje recibido
            System.out.println("Procesando mensaje: " + productMessage);
            System.out.println("ID del producto: " + productMessage.getProductId());
            System.out.println("Nombre: " + productMessage.getName());
            System.out.println("Stock: " + productMessage.getStock());

            logger.info("Mensaje procesado correctamente");
        } catch (Exception e) {
            logger.error("Error al procesar mensaje: ", e);
        }
    }
*/