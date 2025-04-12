package org.example.springboottest.User.Entity;


import lombok.Data;
import jakarta.persistence.*;

@Entity
@Data
@Table(name = "User")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 自动生成并自增
    @Column(name = "user_id")
    private Long userId;  // 将 userId 改为 Long 类型

    private String username; // 主键

    private String password;

    private String email;

    private String identity;

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

}