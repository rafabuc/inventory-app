package com.rbm.inventory.init;

import com.rbm.inventory.model.Inventory;
import com.rbm.inventory.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Realiza una carga de datos en la base H2 en memoria de la aplicacion
 */
@Configuration
public class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    /**
     * Metodo para inicializar la base de datos con informacion de prueba.
     * <p>
     * Utiliza la interfaz CommandLineRunner para ejecutar el metodo initDatabase al
     * inicio de la aplicacion.
     * <p>
     * Si no hay inventario de productos en la base de datos, crea 10 inventarios de producto de prueba y los guarda
     * en la base de datos. Muestra un mensaje en el log con la informacion de cada
     * inventario guardado.
     */
    @Bean
    CommandLineRunner initDatabase(InventoryRepository inventoryRepository) {
        return args -> {
            if (inventoryRepository.count() == 0) {

                Random random = new Random();
                List<Inventory> inventoryList = new ArrayList<Inventory>();
                for (int i = 0; i < 20; i++) {

                    int numeroAleatorio = random.nextInt(10) + 1;

                    inventoryList.add(new Inventory("name_inventory" + i, Long.valueOf(i), numeroAleatorio));
                }

                for (Inventory inventory : inventoryList) {
                    log.info("Carga inicial: {}", inventoryRepository.save(inventory));
                }
            }
        };
    }

}
