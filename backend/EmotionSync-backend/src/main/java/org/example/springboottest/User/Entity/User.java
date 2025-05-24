package org.example.springboottest.User.Entity;


import lombok.Data;
import jakarta.persistence.*;
/**
 * 用户实体类
 * 对应数据库中的 User 表
 */
@Entity
@Data
@Table(name = "User")
public class User {
    /**
     * 用户 ID，主键，自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 自动生成并自增
    @Column(name = "user_id")
    private Long userId;
    /**
     * 用户名
     */
    private String username; // 主键
    /**
     * 密码
     */
    private String password;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 身份信息（如用户、管理员）
     */
    private String identity;



}