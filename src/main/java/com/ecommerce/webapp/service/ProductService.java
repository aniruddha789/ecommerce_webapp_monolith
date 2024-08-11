package com.ecommerce.webapp.service;


import com.ecommerce.webapp.dto.response.Status;
import com.ecommerce.webapp.entity.Product;
import com.ecommerce.webapp.repository.ProductRepository;
import com.ecommerce.webapp.util.StatusBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;


    public Status addProduct(RequestEntity<Product> productRequestEntity){

        Product newProduct = productRequestEntity.getBody();

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

}
