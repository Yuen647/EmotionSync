package org.example.springboottest.Note;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {
    @Autowired
    private NoteService noteService;

    @PostMapping
    public ResponseEntity<Note> createNote(@RequestBody Note note) {
        Note createdNote = noteService.createNote(note);
        return ResponseEntity.ok(createdNote);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<Note>> getNotesByUsername(@PathVariable String username) {
        List<Note> notes = noteService.getNotesByUsername(username);
        return ResponseEntity.ok(notes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable Integer id, @RequestBody Note note) {
        Note updatedNote = noteService.updateNote(id, note);
        if (updatedNote != null) {
            return ResponseEntity.ok(updatedNote);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Integer id) {
        boolean deleted = noteService.deleteNote(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
} 