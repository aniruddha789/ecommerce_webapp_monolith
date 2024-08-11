package com.ecommerce.webapp.repository;

import com.ecommerce.webapp.entity.Address;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface AddressRepository extends CrudRepository<Address, Integer> {



    ArrayList<Address> findAll();

    Address findById(int id);




}
