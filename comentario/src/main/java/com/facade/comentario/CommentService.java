package com.facade.comentario;

import com.facade.comentario.Comment;
import com.facade.comentario.CommentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    // Usa uma lista em memória para simular um banco de dados.
    private final List<Comment> comments = new CopyOnWriteArrayList<>();
    // Contador atômico para gerar IDs únicos de forma segura.
    private final AtomicLong counter = new AtomicLong();

    @PostConstruct
    public void init() {
        // Inicializa o serviço com alguns dados de exemplo após a construção do bean.
        logger.info("Inicializando CommentService com dados de exemplo.");
        comments.add(new Comment(counter.incrementAndGet(), "Alice", "Ótimo post!", Instant.now()));
        comments.add(new Comment(counter.incrementAndGet(), "Bob", "Concordo plenamente com a Alice.", Instant.now()));
        comments.add(new Comment(counter.incrementAndGet(), "Charlie", "Muito útil, obrigado!", Instant.now()));
    }

    /**
     * Retorna uma lista de comentários.
     * Em uma aplicação real, estes dados viriam de um banco de dados.
     */
    public List<Comment> getAllComments() {
        logger.debug("Recuperando todos os {} comentários da lista em memória.", comments.size());
        return new ArrayList<>(comments); // Retorna uma cópia para proteger a lista interna.
    }

    public Comment addComment(CommentRequest request) {
        Comment newComment = new Comment(counter.incrementAndGet(), request.author(), request.content(), Instant.now());
        logger.info("Novo comentário criado com ID {}: Autor='{}'", newComment.id(), newComment.author());
        comments.add(newComment);
        logger.debug("Comentário {} adicionado à lista. Tamanho atual da lista: {}", newComment.id(), comments.size());
        return newComment;
    }
}