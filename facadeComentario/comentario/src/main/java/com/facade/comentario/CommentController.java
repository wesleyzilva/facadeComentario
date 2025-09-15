package com.facade.comentario.controller;

import com.facade.comentario.model.Comment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api") // Define o prefixo /api para todos os endpoints neste controlador
public class CommentController {

    @GetMapping("/comments") // Mapeia para /api/comments
    public List<Comment> getAllComments() {
        // Para demonstração, retornamos uma lista de comentários hardcoded.
        // Em uma aplicação real, você buscaria isso de um serviço ou banco de dados.
        Comment comment1 = new Comment(1L, "Alice", "Ótimo post!", "2023-10-26T10:00:00Z");
        Comment comment2 = new Comment(2L, "Bob", "Concordo plenamente com a Alice.", "2023-10-26T10:05:00Z");
        Comment comment3 = new Comment(3L, "Charlie", "Muito útil, obrigado!", "2023-10-26T10:15:00Z");
        return Arrays.asList(comment1, comment2, comment3);
    }

    // Você pode adicionar outros endpoints aqui, como @PostMapping para criar um comentário, etc.
}