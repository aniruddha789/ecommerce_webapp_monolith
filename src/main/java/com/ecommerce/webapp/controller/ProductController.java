package com.ecommerce.webapp.controller;

import com.ecommerce.webapp.dto.response.Status;
import com.ecommerce.webapp.entity.Product;
import com.ecommerce.webapp.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.*;

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



}
