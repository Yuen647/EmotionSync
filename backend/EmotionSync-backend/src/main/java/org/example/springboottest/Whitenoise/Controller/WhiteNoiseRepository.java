package org.example.springboottest.Whitenoise.Controller;

import org.example.springboottest.Whitenoise.Entity.WhiteNoise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface WhiteNoiseRepository extends JpaRepository<WhiteNoise, String> {
    List<WhiteNoise> findAllByUsername(String Username);

    List<WhiteNoise> findAllByUsernameAndStarttimeBetween(String Username, LocalDateTime startTime, LocalDateTime endTime);
}