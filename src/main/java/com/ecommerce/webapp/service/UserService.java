package com.ecommerce.webapp.service;


import com.ecommerce.webapp.dto.request.LoginDTO;
import com.ecommerce.webapp.dto.response.Status;
import com.ecommerce.webapp.dto.request.RegisterDTO;
import com.ecommerce.webapp.entity.*;
import com.ecommerce.webapp.repository.ShopOrderRepository;
import com.ecommerce.webapp.repository.UserEntityRepository;
import com.ecommerce.webapp.util.StatusBuilder;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserEntityRepository userEntityRepository;

    @Autowired
    ShopOrderRepository shopOrderRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptEncoder;

    public UserEntity findById(Integer id) {
        return userEntityRepository.findById(id).get();
    }

    public UserEntity findByUsername(String username) {
        return userEntityRepository.findByUsername(username);
    }

    public UserEntity findByEmail(String email) {
        return userEntityRepository.findByEmail(email);
    }

    public ArrayList<Role> getRoles(String username) {
        UserEntity user = this.userEntityRepository.findByUsername(username);
        return (ArrayList<Role>) user.getRoles();
    }

    public ArrayList<UserEntity> getAllUsers(){
        return userEntityRepository.findAll();
    }

    @Transactional
    public Status deleteUser(LoginDTO loginDTO) {
        UserEntity findUser = userEntityRepository.findByUsername(loginDTO.getUsername());

        if(findUser == null){
            return new StatusBuilder()
                    .status("FAIL")
                    .code("402")
                    .message("User does not exist")
                    .build();

        } else if (!bCryptEncoder.matches(loginDTO.getPassword(), findUser.getPassword())) {
            return new StatusBuilder()
                    .status("FAIL")
                    .code("403")
                    .message("Wrong password!")
                    .build();
        }

        userEntityRepository.deleteByUsername(loginDTO.getUsername());

        return new StatusBuilder()
                .status("SUCCESSFUL")
                .code("200")
                .message("User deleted successfully!")
                .build();
    }

    public Status register(RegisterDTO registerDTO){

        UserEntity findUser = findByUsername(registerDTO.getUsername());
        if(findUser != null){
            return new StatusBuilder()
                    .status("FAIL")
                    .message("Username already exists!")
                    .code("400")
                    .build();
        }

        findUser = findByEmail(registerDTO.getEmail());
        if(findUser != null){
            return new StatusBuilder()
                    .status("FAIL")
                    .message("Email already exists!")
                    .code("401")
                    .build();
        }

        UserEntity userEntity = new UserEntity(registerDTO.getFirstname(),
                registerDTO.getLastname(),
                registerDTO.getUsername(),
                bCryptEncoder.encode(registerDTO.getPassword()),
                registerDTO.getEmail(),
                registerDTO.getPhone());

        /** Set firebase UID to link with firebase user */
        userEntity.setFirebaseUid(registerDTO.getFirebaseUid());

        //set the roles
        userEntity.setRoles(registerDTO.getRoles());

        userEntityRepository.save(userEntity);

        return new StatusBuilder()
                .status("SUCCESS")
                .message("User created successfully!")
                .code("200")
                .build();
    }


    public Status loginUser(LoginDTO loginDTO) {
        UserEntity findUser = userEntityRepository.findByUsername(loginDTO.getUsername());

        if(findUser == null){
            return new StatusBuilder()
                    .status("FAIL")
                    .code("402")
                    .message("User does not exist")
                    .build();

        } else if (!bCryptEncoder.matches(loginDTO.getPassword(), findUser.getPassword())) {
            return new StatusBuilder()
                    .status("FAIL")
                    .code("403")
                    .message("Wrong password!")
                    .build();
        }

        return new StatusBuilder()
                .status("SUCCESS")
                .code("200")
                .message("User logged in successfully!")
                .build();

    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = findByUsername(username);
        User springUser = null;

        if(userEntity == null){
            throw new UsernameNotFoundException("User: " + username + " not found!");
        } else {

            List<Role> roles = userEntity.getRoles();
            Set<GrantedAuthority> ga = new HashSet<>();
            for (Role role : roles) {
                ga.add(new SimpleGrantedAuthority(role.getName()));
            }

            springUser = new User(userEntity.getUsername(), userEntity.getPassword(), ga);

        }

        return springUser;

    }


    public Set<Address> getAddress(String username){

        UserEntity user = this.userEntityRepository.findByUsername(username);

        return user.getAddresses();
    }

    /*
    Is there a different way to do this?
    Can we just have a OrderService which returns Orders by userID
     */
    public ArrayList<ShopOrder> getAllOrders(String username){

        UserEntity user = this.userEntityRepository.findByUsername(username);

        return (ArrayList<ShopOrder>) user.getOrders();

    }

    public Optional<ShopOrder> getOrderByID(int orderId){

        return this.shopOrderRepository.findById(orderId);
    }

    public ShopOrder saveOrder(ShopOrder order){

        return shopOrderRepository.save(order);
    }

    public Status addAddressToUser(String username, Address address) {
        UserEntity user = userEntityRepository.findByUsername(username);

        if (user == null) {
            return new StatusBuilder()
                    .status("FAIL")
                    .code("404")
                    .message("User not found")
                    .build();
        }

        user.addAddress(address);
        userEntityRepository.save(user);

        return new StatusBuilder()
                .status("SUCCESS")
                .code("200")
                .message("Address added successfully")
                .build();
    }


}
