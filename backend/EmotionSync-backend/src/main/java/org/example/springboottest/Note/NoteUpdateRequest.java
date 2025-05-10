package org.example.springboottest.Note;

import lombok.Data;

@Data
public class NoteUpdateRequest {
    private Integer id;
    private Note note;
} 