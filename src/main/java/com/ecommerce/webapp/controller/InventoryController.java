package com.ecommerce.webapp.controller;

import com.ecommerce.webapp.dto.request.AddInventoryDTO;
import com.ecommerce.webapp.dto.response.Status;
import com.ecommerce.webapp.entity.Inventory;
import com.ecommerce.webapp.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    InventoryService inventoryService;

    @PostMapping("/add")
    public Status addInventory(@RequestBody AddInventoryDTO inventoryRequest){

        Status resp = this.inventoryService.addInventory(inventoryRequest);
        return resp;

    }

    @PostMapping("/delete")
    public Status deleteInventory(Inventory inventory) {
        return this.inventoryService.deleteInventory(inventory);
    }

    @PostMapping("/update")
    public Status updateInventory(Inventory inventory) {
        return this.inventoryService.updateInventory(inventory);
    }



}
