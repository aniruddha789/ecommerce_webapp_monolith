package com.ecommerce.webapp.repository;

import com.ecommerce.webapp.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface ProductRepository extends CrudRepository<Product, Integer> {

    ArrayList<Product> findAll();

    Product findById(int id);

    Product findByName(String name);

    Product save(Product product);

    void deleteById(int id);

    Page<Product> findAll(Pageable pageable);

    ArrayList<Product> findAllByType(String type);

    ArrayList<Product> findAllByBrandid(String brandid);

    Product findByNameAndBrandid(String name, String brand);

    Page<Product> findAllByType(String type, Pageable pageable);


}
