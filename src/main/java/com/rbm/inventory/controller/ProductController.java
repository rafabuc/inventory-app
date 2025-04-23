package com.rbm.inventory.controller;

import com.rbm.inventory.model.Product;
import com.rbm.inventory.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para la entidad @see {@link Product}
 * Expone los siguientes metodos:
 * <ul>
 *     <li>Obtener la lista de todos los productos</li>
 *     <li>Buscar un producto por id</li>
 *     <li>Crear nuevo producto</li>
 *     <li>Editar un producto</li>
 *     <li>Borrar u producto por id</li>
 * </ul>
 */
@RestController
public class ProductController {


    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Devuelve una lista de todos los productos
     *
     * @return @{@link ResponseEntity} con la lista de productos
     */
    @GetMapping("/products")
    public ResponseEntity<List<Product>> listAll() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    /**
     * Devuelve un {@link Product} por ID
     *
     * @param id el identificador del producto
     * @return @{@link ResponseEntity} con el producto encontrado, en caso de no
     * encontrarlo devuelve estado de not found
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<Product> findById(@PathVariable Long id) {
        Optional<Product> product = productRepository.findById(id);
        return product.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Crea un {@link Product}
     *
     * @param newProduct el {@link Product} que se crea
     * @param ucb
     * @return {@link ResponseEntity} con la ubicacion del producto creado
     */
    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody Product newProduct, UriComponentsBuilder ucb) {
        Product saved_product = productRepository.save(newProduct);

        URI uriProduct = ucb
                .path("products/{id}")
                .buildAndExpand(saved_product.getId())
                .toUri();

        return ResponseEntity.created(uriProduct).build();
    }

    /**
     * Actualiza unn {@link Product}
     *
     * @param productToUpdate el producto que se actualizara
     * @param id              identificador del producto qyue se actualiza
     * @return {@link ResponseEntity} con el producto actualizado , en caso de no encontrarlo se devuelve estado de no encontrado
     */
    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(@RequestBody Product productToUpdate, @PathVariable Long id) {

        return ResponseEntity.ok(productRepository.findById(id)
                .map(product -> {
                    product.setCode(productToUpdate.getCode());
                    product.setDescription(productToUpdate.getDescription());
                    product.setName(productToUpdate.getName());
                    product.setWeight(productToUpdate.getWeight());
                    productRepository.save(product);
                    return ResponseEntity.ok(product);
                }).orElseGet(() -> {
                    //productRepository.save(productToUpdate);
                    return ResponseEntity.noContent().build();
                }));

    }

    /**
     * Elimina un  {@link Product} por id
     *
     * @param id
     * @return
     */
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
