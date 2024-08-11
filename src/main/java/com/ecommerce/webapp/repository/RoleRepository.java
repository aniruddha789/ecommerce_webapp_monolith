package com.ecommerce.webapp.repository;

import com.ecommerce.webapp.entity.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface RoleRepository extends CrudRepository<Role, Integer> {

    ArrayList<Role> findAll();

    Role findByName(String name);

    Role findById(int id);

    Role save(Role role);

    void deleteById(int id);


}
