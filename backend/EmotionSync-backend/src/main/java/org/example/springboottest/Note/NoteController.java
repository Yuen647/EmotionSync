package org.example.springboottest.Note;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notes")
public class NoteController {
    @Autowired
    private NoteService noteService;

    @PostMapping("/create")
    public ResponseEntity<Note> createNote(@RequestBody Note note) {
        Note createdNote = noteService.createNote(note);
        return ResponseEntity.ok(createdNote);
    }

    @PostMapping("/list")
    public ResponseEntity<List<Note>> getNotesByUsername(@RequestBody NoteRequest request) {
        List<Note> notes = noteService.getNotesByUsername(request.getUsername());
        return ResponseEntity.ok(notes);
    }

    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateNote(@RequestBody NoteUpdateRequest request) {
        Note updatedNote = noteService.updateNote(request.getId(), request.getNote());
        if (updatedNote != null) {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "笔记更新成功",
                "data", updatedNote
            ));
        } else {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "笔记不存在"
            ));
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteNote(@RequestBody NoteDeleteRequest request) {
        boolean deleted = noteService.deleteNote(request.getId());
        if (deleted) {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "笔记删除成功"
            ));
        } else {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "笔记不存在"
            ));
        }
    }
} 