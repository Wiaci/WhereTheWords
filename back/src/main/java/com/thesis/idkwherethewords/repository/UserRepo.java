package com.thesis.idkwherethewords.repository;

import com.thesis.idkwherethewords.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    User findByName(String name);
    boolean existsByName(String name);
}
