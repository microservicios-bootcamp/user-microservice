package com.demo.app.user.services.impl;

import com.demo.app.user.entities.User;
import com.demo.app.user.repositories.UserRepository;
import com.demo.app.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        List<User> list = userRepository.findAll();
        return list;
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }
}
