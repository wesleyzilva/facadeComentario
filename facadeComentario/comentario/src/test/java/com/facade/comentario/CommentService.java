package com.facade.comentario.service;

import com.facade.comentario.model.Comment;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Service
public class CommentService {

    /**
     * Retorna uma lista de comentários.
     * Em uma aplicação real, estes dados viriam de um banco de dados.
     */
    public List<Comment> getAllComments() {
        return Arrays.asList(
                new Comment(1L, "Alice", "Ótimo post!", Instant.parse("2023-10-26T10:00:00Z")),
                new Comment(2L, "Bob", "Concordo plenamente com a Alice.", Instant.parse("2023-10-26T10:05:00Z")),
                new Comment(3L, "Charlie", "Muito útil, obrigado!", Instant.parse("2023-10-26T10:15:00Z"))
        );
    }
}