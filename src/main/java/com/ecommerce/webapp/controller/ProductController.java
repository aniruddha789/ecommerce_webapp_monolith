package com.ecommerce.webapp.controller;

import com.ecommerce.webapp.dto.response.Status;
import com.ecommerce.webapp.entity.Inventory;
import com.ecommerce.webapp.entity.Product;
import com.ecommerce.webapp.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/product")
@RestController
public class ProductController {

    @Autowired
    ProductService productService;

    @PostMapping("/addProduct")
    public Status addProduct(RequestEntity<Product> productRequestEntity){

        Status resp = this.productService.addProduct(productRequestEntity);
        return resp;
    }

    @PostMapping("/deleteProduct/{id}")
    public Status deleteProduct(@PathVariable Integer id){
        return this.productService.deleteProduct(id);
    }

    @GetMapping("/getProduct")
    public ArrayList<Product> getAllProducts(){
        return this.productService.getAllProducts();
    }

    @GetMapping("/getProduct/{id}")
    public Product getProductById(@PathVariable Integer id){
        return this.productService.getProductById(id);
    }

    @GetMapping("/getProductByType/{type}")
    public ArrayList<Product> getProductByType(@PathVariable String type){
        return this.productService.getProductByType(type);
    }

    @GetMapping("/getInventory/{pid}")
    public List<Inventory> getProductInventory(@PathVariable int pid){ //pid - product ID

        return this.productService.getProductInventory(pid);
    }

    @GetMapping("/getProductByBrand/{brand}")
    public ArrayList<Product> getProductByBrand(@PathVariable String brand){
        return this.productService.getProductByBrand(brand);
    }


}
