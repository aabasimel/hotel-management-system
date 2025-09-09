package com.ahmedabasimel.myhotel.service;

import com.ahmedabasimel.myhotel.models.User;

import java.util.List;

public interface UserService {

    User registerUser(User user);

    List<User> getUsers();

    void deleteUser(String email);

    User getUser(String email);




}
