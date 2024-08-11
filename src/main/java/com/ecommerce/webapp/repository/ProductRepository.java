package com.ecommerce.webapp.repository;

import com.ecommerce.webapp.entity.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface ProductRepository extends CrudRepository<Product, Integer> {

    ArrayList<Product> findAll();

    Product findById(int id);

    Product findByName(String name);

    Product findByBrandid(String brandid);


}
