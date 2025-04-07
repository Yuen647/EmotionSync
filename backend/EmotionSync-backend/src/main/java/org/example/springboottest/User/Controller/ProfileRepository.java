package org.example.springboottest.User.Controller;

import org.example.springboottest.User.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<User, String> {
}
