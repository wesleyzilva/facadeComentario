package com.facade.comentario.controller;

import com.facade.comentario.model.Comment;
import com.facade.comentario.model.CommentRequest;
import com.facade.comentario.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200") // Permite requisições do frontend Angular
@RequestMapping("/api") // Define o prefixo /api para todos os endpoints neste controlador
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/comments") // Mapeia para /api/comments
    public List<Comment> getAllComments() {
        logger.info("Requisição recebida para buscar todos os comentários.");
        return commentService.getAllComments();
    }

    @PostMapping("/comments")
    public ResponseEntity<Comment> createComment(@RequestBody CommentRequest commentRequest) {
        logger.info("Requisição recebida para criar um novo comentário por '{}'", commentRequest.author());
        Comment createdComment = commentService.addComment(commentRequest);
        // Retorna 201 Created com o comentário criado no corpo da resposta.
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }
}