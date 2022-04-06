package com.demo.app.user.controllers;

import com.demo.app.user.entities.User;
import com.demo.app.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping
    private ResponseEntity<List<User>> findAll(){
        List<User> user = userService.findAll();
        return ResponseEntity.ok(user);
    }

    @PostMapping
    private ResponseEntity<User> save(@RequestBody User user){
        return ResponseEntity.ok(userService.save(user));
    }
}
