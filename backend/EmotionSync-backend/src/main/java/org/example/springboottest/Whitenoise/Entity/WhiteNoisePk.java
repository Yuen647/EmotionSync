package org.example.springboottest.Whitenoise.Entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Embeddable
public class WhiteNoisePk implements Serializable {
    private String username;
    private String audio_name;
    private int last_time;
    private String emotion;

    public WhiteNoisePk() {}
}