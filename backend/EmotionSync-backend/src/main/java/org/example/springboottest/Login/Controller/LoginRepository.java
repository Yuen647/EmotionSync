package org.example.springboottest.Login.Controller;

import org.example.springboottest.User.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
/**
 * LoginRepository 接口继承 JpaRepository，用于操作 User 实体类的数据持久化
 * 提供基本的 CRUD 操作外，还定义了几个根据用户名、邮箱等条件查询的方法
 * 泛型参数：
 *  - Audio：实体类型
 *  - String：实体主键类型
 */
public interface LoginRepository extends JpaRepository<User, String> {
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return Optional 包裹的 User 对象，若用户不存在则为空
     */
    Optional<User> findByUsername(String username);
    /**
     * 判断用户名是否已存在
     * @param username 用户名
     * @return 若存在返回 true，否则 false
     */
    boolean existsByUsername(String username);
    /**
     * 判断邮箱和用户名的组合是否已存在
     * @param email 邮箱
     * @param username 用户名
     * @return 若存在返回 true，否则 false
     */
    boolean existsByEmailAndUsername(String email, String username);
    /**
     * 根据邮箱和用户名查询用户
     * @param email 邮箱
     * @param username 用户名
     * @return Optional 包裹的 User 对象，若用户不存在则为空
     */
    Optional<User> findByEmailAndUsername(String email, String username);
}
