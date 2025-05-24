package org.example.springboottest.Whitenoise.Controller;

import org.example.springboottest.Whitenoise.Entity.WhiteNoise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
/**
 * WhiteNoiseRepository 接口
 * 继承 JpaRepository，实现对白噪声实体的数据库操作
 * 泛型参数：
 *  - WhiteNoise：实体类型
 *  - String：实体主键类型
 */
public interface WhiteNoiseRepository extends JpaRepository<WhiteNoise, String> {

}