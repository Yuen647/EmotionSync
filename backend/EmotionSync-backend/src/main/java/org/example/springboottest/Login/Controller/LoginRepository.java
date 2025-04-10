package org.example.springboottest.Login.Controller;

import org.example.springboottest.User.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginRepository extends JpaRepository<User, String> {
    // 自定义方法，根据 password 和 email 查找用户
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmailAndUsername(String email, String username);
    Optional<User> findByEmailAndUsername(String email, String username);
}
