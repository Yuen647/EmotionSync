package org.example.springboottest.User.Controller;

import org.example.springboottest.User.Entity.Identity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdentityRepository extends JpaRepository<Identity, String> {
}
