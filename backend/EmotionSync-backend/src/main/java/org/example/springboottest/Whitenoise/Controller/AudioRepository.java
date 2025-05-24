package org.example.springboottest.Whitenoise.Controller;

import org.example.springboottest.Whitenoise.Entity.Audio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
/**
 * AudioRepository 接口
 * 继承 JpaRepository，实现对音频实体的数据库操作
 * 泛型参数：
 *  - Audio：实体类型
 *  - String：实体主键类型
 */
public interface AudioRepository extends JpaRepository<Audio, String> {
    /**
     * 查询所有音频记录
     * @return 返回包含所有 Audio 实体的列表
     */
    List<Audio> findAll();
    /**
     * 根据音频名称查找对应的音频实体
     * @param audioName 音频名称
     * @return 返回匹配的 Audio 实体，如果不存在则返回 null
     */
    Audio findByAudioName(String audioName);
}
