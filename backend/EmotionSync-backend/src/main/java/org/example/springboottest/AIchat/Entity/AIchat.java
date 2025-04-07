package org.example.springboottest.AIchat.Entity;


import com.baomidou.mybatisplus.annotation.IdType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
@Table(name = "aichat")
public class AIchat {
    @Id
    private String username; // 主键

    private String chatmessage;

}