package org.example.springboottest.User.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "Identity")
public class Identity {
    @Id
    private String username;

    private int E;

    private int I;

    private int S;

    private int N;

    private int T;

    private int F;

    private int J;

    private int P;
}
