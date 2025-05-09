package org.example.springboottest.Whitenoise.Controller;

import org.example.springboottest.Whitenoise.Entity.Audio;
import org.example.springboottest.Whitenoise.Service.EmotionRecommender;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AudioRepository extends JpaRepository<Audio, String> {
    List<Audio> findAll();
    Audio findByAudioName(String audioName);
}
