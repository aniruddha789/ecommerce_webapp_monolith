package com.ecommerce.webapp.service;


import com.ecommerce.webapp.dto.response.Status;
import com.ecommerce.webapp.entity.Inventory;
import com.ecommerce.webapp.entity.Product;
import com.ecommerce.webapp.repository.ProductRepository;
import com.ecommerce.webapp.util.StatusBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;


    public Status addProduct(Product newProduct){

        Product existingProduct = productRepository.findByNameAndBrandid(
                newProduct.getName(),
                newProduct.getBrandid()
        );

        String prodUniqueKey = newProduct.getName() + "-" + newProduct.getBrandid() ;

        if(existingProduct != null){
            return new StatusBuilder()
                    .status("FAIL")
                    .code("400")
                    .message(String.format("Product %s already exists!", prodUniqueKey))
                    .build();
        }

        try {
            productRepository.save(newProduct);
        } catch (Exception e){
            return new StatusBuilder()
                    .status("FAIL")
                    .code("400")
                    .message(String.format("Product %s could not be created! Error: {}", prodUniqueKey, e.getMessage()))
                    .build();
        }

        return new StatusBuilder()
                .status("PASS")
                .code("200")
                .message("Product Created Successfully!")
                .build();
    }

    public ArrayList<Status> addMultipleProducts(ArrayList<Product> productList){

        ArrayList<Status> statusList = new ArrayList<>();

        for(Product prod : productList){
            Status st = this.addProduct(prod);
            statusList.add(st);
        }
        
        return statusList;
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

    public Page<Product> getAllProductsPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return productRepository.findAll(pageable);
    }

    public Page<Product> getProductByTypePaged(int page, int size, String type) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return productRepository.findAllByType(type, pageable);
    }

    public ArrayList<Product> getSearchResults(String query){
        return productRepository.getSearchResults(query);
    }

}
