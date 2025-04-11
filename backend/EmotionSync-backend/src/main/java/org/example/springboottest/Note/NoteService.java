package org.example.springboottest.Note;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class NoteService {
    @Autowired
    private NoteRepository noteRepository;

    public Note createNote(Note note) {
        return noteRepository.save(note);
    }

    public List<Note> getNotesByUsername(String username) {
        return noteRepository.findByUsername(username);
    }

    public Note updateNote(Integer id, Note note) {
        Optional<Note> existingNote = noteRepository.findById(id);
        if (existingNote.isPresent()) {
            Note noteToUpdate = existingNote.get();
            // 只更新允许修改的字段
            if (note.getDescription() != null) {
                noteToUpdate.setDescription(note.getDescription());
            }
            if (note.getImageUrls() != null) {
                noteToUpdate.setImageUrls(note.getImageUrls());
            }
            return noteRepository.save(noteToUpdate);
        }
        return null;
    }

    public boolean deleteNote(Integer id) {
        if (noteRepository.existsById(id)) {
            noteRepository.deleteById(id);
            return true;
        }
        return false;
    }
} 