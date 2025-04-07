package org.example.springboottest.AIchat.Controller;

import org.example.springboottest.AIchat.Entity.AIchat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AIchatRepository extends JpaRepository<AIchat, String> {
}
