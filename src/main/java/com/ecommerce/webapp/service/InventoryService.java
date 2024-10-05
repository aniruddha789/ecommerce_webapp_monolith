package com.ecommerce.webapp.service;

import com.ecommerce.webapp.dto.request.AddInventoryDTO;
import com.ecommerce.webapp.dto.response.Status;
import com.ecommerce.webapp.entity.Inventory;
import com.ecommerce.webapp.repository.InventoryRepository;
import com.ecommerce.webapp.repository.ProductRepository;
import com.ecommerce.webapp.util.StatusBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    @Autowired
    InventoryRepository inventoryRepository;

    @Autowired
    ProductRepository productRepository;

    public Status addInventory(AddInventoryDTO inventoryRequest) {
        try{
            Inventory inventory = new Inventory();
            inventory.setProduct(productRepository.findById(inventoryRequest.getProductID()));
            inventory.setSize(inventoryRequest.getSize());
            inventory.setQuantity(inventoryRequest.getQuantity());
            inventoryRepository.save(inventory);
        } catch (Exception e) {
            return new StatusBuilder()
                    .status("FAIL")
                    .code("400")
                    .message(e.getMessage())
                    .build();
        }

        return new StatusBuilder()
                .status("PASS")
                .code("200")
                .message("Inventory Created Successfully!")
                .build();
    }

    public Status updateInventory(Inventory inventory) {
        try{
            inventoryRepository.save(inventory);
        } catch (Exception e) {
            return new StatusBuilder()
                    .status("FAIL")
                    .code("400")
                    .message(e.getMessage())
                    .build();
        }

        return new StatusBuilder()
                .status("PASS")
                .code("200")
                .message("Inventory Created Successfully!")
                .build();
    }

    public Status deleteInventory(Inventory inventory) {
        try{
            inventoryRepository.delete(inventory);
        } catch (Exception e) {
            return new StatusBuilder()
                    .status("FAIL")
                    .code("400")
                    .message(e.getMessage())
                    .build();
        }

        return new StatusBuilder()
                .status("PASS")
                .code("200")
                .message("Inventory Created Successfully!")
                .build();
    }

}
