package com.ahmedabasimel.myhotel.controller;

import com.ahmedabasimel.myhotel.exception.UserAlreadyExistsException;
import com.ahmedabasimel.myhotel.models.User;
import com.ahmedabasimel.myhotel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(User user){
        try{

            userService.registerUser(user);
            return ResponseEntity.ok("user registered successfully");



        }catch (UserAlreadyExistsException e){

            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());


        }
    }

}
