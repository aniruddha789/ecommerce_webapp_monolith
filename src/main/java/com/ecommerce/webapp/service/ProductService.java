package com.ecommerce.webapp.service;


import com.ecommerce.webapp.dto.response.Status;
import com.ecommerce.webapp.entity.Inventory;
import com.ecommerce.webapp.entity.Product;
import com.ecommerce.webapp.repository.ProductRepository;
import com.ecommerce.webapp.util.StatusBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;


    public Status addProduct(RequestEntity<Product> productRequestEntity){

        Product newProduct = productRequestEntity.getBody();
        Product existingProduct = productRepository.findByNameAndBrandidAndColor(
                newProduct.getName(),
                newProduct.getBrandid(),
                newProduct.getColor()
        );

        if(existingProduct != null){
            return new StatusBuilder()
                    .status("FAIL")
                    .code("400")
                    .message("Product already exists!")
                    .build();
        }


        productRepository.save(newProduct);

        return new StatusBuilder()
                .status("PASS")
                .code("200")
                .message("Product Created Successfully!")
                .build();
    }

    public Status deleteProduct(int id){
        String msg = "Product deleted successfully!";

        boolean prodExists = this.productRepository.existsById(id);

        if(prodExists)
            this.productRepository.deleteById(id);
        else
            msg = "Product with given id " + id + " does not exist!";

        return new StatusBuilder()
                .status("PASS")
                .code("200")
                .message(msg)
                .build();
    }

    public ArrayList<Product> getAllProducts(){
        return this.productRepository.findAll();
    }

    public Product getProductById(int id){
        return this.productRepository.findById(id);
    }

    public ArrayList<Product> getProductByType(String type){
        return this.productRepository.findAllByType(type);
    }

    public List<Inventory> getProductInventory(int productId){
        Product prod = this.productRepository.findById(productId);

        return prod.getInventory();
    }

    public ArrayList<Product> getProductByBrand(String brand){
        return this.productRepository.findAllByBrandid(brand);
    }

}
