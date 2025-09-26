package com.facade.comentario;

import com.facade.comentario.Comment;
import com.facade.comentario.CommentRequest;
import com.facade.comentario.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
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
        List<Comment> comments = commentService.getAllComments();
        // Loga a quantidade e o conteúdo da lista de comentários que será retornada.
        logger.info("Retornando {} comentários: {}", comments.size(), comments);
        return comments;
    }

    @PostMapping("/comments")
    public ResponseEntity<Comment> createComment(@Valid @RequestBody CommentRequest commentRequest) {
        // Loga tanto o autor quanto o conteúdo da mensagem recebida.
        logger.info("Requisição para criar comentário. Autor: '{}', Conteúdo: '{}'", commentRequest.author(), commentRequest.content());
        Comment createdComment = commentService.addComment(commentRequest);
        logger.info("Comentário criado com sucesso: {}", createdComment);
        // Retorna 201 Created com o comentário criado no corpo da resposta.
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }
}