package com.ecommerce.webapp.repository;

import com.ecommerce.webapp.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface UserEntityRepository extends CrudRepository<UserEntity, Integer> {

    UserEntity findByEmail(String email);

    UserEntity findByUsername(String username);

    void deleteByUsername(String username);

    ArrayList<UserEntity> findAll();

    UserEntity save(UserEntity userEntity);

    void deleteById(int id);


}
