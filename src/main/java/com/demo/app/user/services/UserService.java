package com.demo.app.user.services;

import com.demo.app.user.entities.User;

import java.util.List;

public interface UserService {
    List<User> findAll();
    User save(User user);
}
