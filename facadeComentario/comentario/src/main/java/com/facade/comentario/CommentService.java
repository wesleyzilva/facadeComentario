package com.facade.comentario.service;

import com.facade.comentario.model.Comment;
import com.facade.comentario.model.CommentRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CommentService {

    // Usa uma lista em memória para simular um banco de dados.
    private final List<Comment> comments = new ArrayList<>();
    // Contador atômico para gerar IDs únicos de forma segura.
    private final AtomicLong counter = new AtomicLong();

    public CommentService() {
        // Inicializa o serviço com alguns dados de exemplo.
        addComment(new CommentRequest("Alice", "Ótimo post!"));
        addComment(new CommentRequest("Bob", "Concordo plenamente com a Alice."));
        addComment(new CommentRequest("Charlie", "Muito útil, obrigado!"));
    }

    /**
     * Retorna uma lista de comentários.
     * Em uma aplicação real, estes dados viriam de um banco de dados.
     */
    public List<Comment> getAllComments() {
        return new ArrayList<>(comments); // Retorna uma cópia para proteger a lista interna.
    }

    public Comment addComment(CommentRequest request) {
        Comment newComment = new Comment(counter.incrementAndGet(), request.author(), request.content(), Instant.now());
        comments.add(newComment);
        return newComment;
    }
}