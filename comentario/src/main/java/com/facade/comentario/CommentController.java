package com.facade.comentario;

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

    @GetMapping("/facadeComments") // Mapeia para /api/facadeComments
    public List<Comment> getAllComments() {
        logger.info("Requisição recebida em /api/facadeComments para buscar todos os comentários.");
        List<Comment> comments = commentService.getAllComments();
        // Loga a quantidade e o conteúdo da lista de comentários que será retornada.
        logger.info("Retornando {} comentários: {}", comments.size(), comments);
        return comments;
    }

    @PostMapping("/facadeComments")
    public ResponseEntity<Comment> createComment(@Valid @RequestBody CommentRequest commentRequest) {
        // Loga tanto o autor quanto o conteúdo da mensagem recebida.
        logger.info("Requisição POST em /api/facadeComments para criar comentário. Autor: '{}', Conteúdo: '{}'", commentRequest.author(), commentRequest.content());
        try {
            Comment createdComment = commentService.addComment(commentRequest);
            // Retorna 201 Created com o comentário criado no corpo da resposta.
            return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
        } catch (ExternalServiceException e) {
            logger.error("Falha ao processar a criação do comentário devido a erro no serviço externo.", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }
}