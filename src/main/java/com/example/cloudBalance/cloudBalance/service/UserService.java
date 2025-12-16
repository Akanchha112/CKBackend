package com.example.cloudBalance.cloudBalance.service;

import com.example.cloudBalance.cloudBalance.model.User;
import com.example.cloudBalance.cloudBalance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    public UserRepository userrepo;
    @Autowired
    public PasswordEncoder passwordEncoder;


    public ResponseEntity<Object> addUser(User user){
        String hashed = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashed);
         userrepo.save(user);
         return new ResponseEntity<>(user.getFirstName(), HttpStatus.CREATED);
    }

    public ResponseEntity<Object> getAllUser(){
        List<User> findAll= userrepo.findAll();

        if(findAll.isEmpty()){
            return new ResponseEntity<>("No User Found",HttpStatus.OK);
        }
        return new ResponseEntity<>(findAll,HttpStatus.OK);
    }

    public ResponseEntity<Object> editUser(Long id,User user){
        Optional<User> findUser=userrepo.findById(id);
        if(findUser.isEmpty()){
            return new ResponseEntity<>(id,HttpStatus.NOT_FOUND);
        }

        User existingUser=findUser.get();
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmailId(user.getEmailId());
        existingUser.setRole(user.getRole());
        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));

        userrepo.save(existingUser);
        return new ResponseEntity<>(existingUser.getFirstName(),HttpStatus.OK);
    }
}
