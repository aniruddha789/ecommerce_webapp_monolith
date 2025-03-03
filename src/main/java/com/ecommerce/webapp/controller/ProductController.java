package com.ecommerce.webapp.controller;

import com.ecommerce.webapp.dto.response.Status;
import com.ecommerce.webapp.entity.Inventory;
import com.ecommerce.webapp.entity.Product;
import com.ecommerce.webapp.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/product")
@RestController
public class ProductController {

    @Autowired
    ProductService productService;

    @Autowired
    private Environment env;

    @PostMapping("/addProduct")
    public Status addProduct(RequestEntity<Product> productRequestEntity){

        Status resp = this.productService.addProduct(productRequestEntity.getBody());
        return resp;
    }

    @PostMapping("/addMultipleProducts")
    public ArrayList<Status> addMultipleProducts(@RequestBody ArrayList<Product> products){
        return this.productService.addMultipleProducts(products);
    }

    @PostMapping("/deleteProduct/{id}")
    public Status deleteProduct(@PathVariable Integer id){
        return this.productService.deleteProduct(id);
    }

    @GetMapping("/getProduct")
    public ArrayList<Product> getAllProducts(){

        System.out.println("\n\nTEST:" + env.getProperty("test") + "\nTMP: " + System.getenv("MYSQL_USER_PASSWORD"));
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

    @GetMapping("/getProductPaged")
    public ResponseEntity<Page<Product>> getPaginatedProducts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<Product> paginatedProducts = productService.getAllProductsPaged(page, size);
        return ResponseEntity.ok().body(paginatedProducts);
    }

    @GetMapping("/getProductByTypePaged/{type}")
    public ResponseEntity<Page<Product>> getProductByTypePaged(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size
    , @PathVariable String type) {
        Page<Product> paginatedProducts = productService.getProductByTypePaged(page, size, type);
        return ResponseEntity.ok().body(paginatedProducts);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> getSearchResults(@RequestParam String query) {
        List<Product> searchResults = productService.getSearchResults(query);
        return ResponseEntity.ok().body(searchResults);
    }

}
