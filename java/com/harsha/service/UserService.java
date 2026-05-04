package com.harsha.service;

import com.harsha.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User saveUser(User user);
    List<User> getAllUsers();
    Optional<User> getUserById(Long id);

    // This is the specific method your DefaultController is looking for
    User findUserByEmail(String email);

    Optional<User> getUserByEmail(String email);
    Optional<User> getUserByPhone(String phone);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    void deleteUser(Long id);
    User updateUser(Long id, User updatedUser);
}