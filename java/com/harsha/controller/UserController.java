package com.harsha.controller;

import com.harsha.entity.User;
import com.harsha.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        if (userService.existsByPhone(user.getPhone())) {
            return ResponseEntity.badRequest().body(null); // Phone already exists
        }
        if (userService.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body(null); // Email already exists
        }
        User savedUser = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        // Check if trying to update to an existing phone
        if (user.getPhone() != null && userService.existsByPhone(user.getPhone())) {
            userService.getUserByPhone(user.getPhone())
                    .ifPresent(existingUser -> {
                        // If it's not the same user, return error
                        if (!existingUser.getId().equals(id)) {
                            throw new IllegalArgumentException("Phone number already in use");
                        }
                    });
        }

        // Check if trying to update to an existing email
        if (user.getEmail() != null && userService.existsByEmail(user.getEmail())) {
            userService.getUserByEmail(user.getEmail())
                    .ifPresent(existingUser -> {
                        // If it's not the same user, return error
                        if (!existingUser.getId().equals(id)) {
                            throw new IllegalArgumentException("Email already in use");
                        }
                    });
        }

        User updatedUser = userService.updateUser(id, user);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.getUserById(id).isPresent()) {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}