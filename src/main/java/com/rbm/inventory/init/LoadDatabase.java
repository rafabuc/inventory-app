package com.rbm.inventory.init;

import com.rbm.inventory.model.Product;
import com.rbm.inventory.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    /**
     * Metodo para inicializar la base de datos con informacion de prueba.
     *
     * Utiliza la interfaz CommandLineRunner para ejecutar el metodo initDatabase al
     * inicio de la aplicacion.
     *
     * Si no hay productos en la base de datos, crea 10 productos de prueba y los guarda
     * en la base de datos. Muestra un mensaje en el log con la informacion de cada
     * producto guardado.
     */
    @Bean
    CommandLineRunner initDatabase(ProductRepository productRepository) {
        return args -> {
            if (productRepository.count() == 0) {
               List<Product> productList= new ArrayList<Product>();
               for (int i=0; i<20; i ++){

                   productList.add(new Product("description_"+i, "name_"+i, "code_"+i, ""+i ));
               }

                for (Product product : productList) {
                    log.info("Carga inicial: {}", productRepository.save(product));
                }
            }
        };
    }

}
