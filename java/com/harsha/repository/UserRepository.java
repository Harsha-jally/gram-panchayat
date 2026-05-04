package com.harsha.repository;

import com.harsha.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List; // ← was missing here
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhone(String phone);
    Optional<User> findByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);

    @Query("SELECT DISTINCT u.pincode FROM User u WHERE u.pincode IS NOT NULL AND u.pincode <> ''")
    List<String> findAllDistinctPincodes();
}