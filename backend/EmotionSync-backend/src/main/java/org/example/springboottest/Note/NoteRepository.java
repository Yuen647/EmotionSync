package org.example.springboottest.Note;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Integer> {
    List<Note> findByUsername(String username);
} 