package com.ahmedabasimel.myhotel.service.impl;

import com.ahmedabasimel.myhotel.exception.UserAlreadyExistsException;
import com.ahmedabasimel.myhotel.models.Role;
import com.ahmedabasimel.myhotel.models.User;
import com.ahmedabasimel.myhotel.repository.RoleRepository;
import com.ahmedabasimel.myhotel.repository.UserRepository;
import com.ahmedabasimel.myhotel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())){
            throw new UserAlreadyExistsException(user.getEmail() + " already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        System.out.println(user.getPassword());
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role ROLE_USER not found in DB"));
        user.setRoles(Collections.singletonList(userRole));
        return userRepository.save(user);
    }

    @Override
    public List<User> getUsers() {

        return userRepository.findAll();
    }

    @Transactional
    @Override
    public void deleteUser(String email) {
        User user = getUser(email);

        if(user!=null){
            userRepository.deleteByEmail(email);

        }




    }

    @Override
    public User getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("user not found "));
    }
}
