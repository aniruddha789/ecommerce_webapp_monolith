package com.ecommerce.webapp.repository;

import com.ecommerce.webapp.entity.Inventory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface InventoryRepository extends CrudRepository<Inventory, Integer> {

    ArrayList<Inventory> findAll();

    Inventory findById(int id);

    ArrayList<Inventory> findByProductId(int productId);

    Inventory save(Inventory inventory);

    void deleteById(int id);

    Optional<Inventory> findByProductIdAndSizeAndColor(int productId, String size, String color);
    
}
