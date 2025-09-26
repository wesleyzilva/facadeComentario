package com.facade.comentario;

import com.facade.comentario.Comment;
import com.facade.comentario.CommentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    @Value("${external.api.comments.url}")
    private String externalApiUrl;

    private final RestTemplate restTemplate;

    // Usa uma lista em memória para simular um banco de dados.
    private final List<Comment> comments = new CopyOnWriteArrayList<>();
    // Contador atômico para gerar IDs únicos de forma segura.
    private final AtomicLong counter = new AtomicLong();

    public CommentService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

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
/*        logger.debug("Recuperando todos os {} comentários da lista em memória.", comments.size());
        return new ArrayList<>(comments); // Retorna uma cópia para proteger a lista interna.
 */

        logger.info("Buscando todos os comentários da camada de persistência: {}", externalApiUrl);
        try {
            Comment[] externalComments = restTemplate.getForObject(externalApiUrl, Comment[].class);
            if (externalComments != null) {
                return Arrays.asList(externalComments);
            }
        } catch (RestClientException e) {
            logger.error("ERRO: Falha ao buscar comentários da camada de persistência na URL '{}'. Causa: {}", externalApiUrl, e.getMessage());
        }
        return new ArrayList<>(); // Retorna uma lista vazia em caso de falha na comunicação.
    }

    public Comment addComment(CommentRequest request) {
        logger.info("Enviando novo comentário para a camada de persistência: {}", externalApiUrl);
        try {
            // Envia o DTO para a API externa e espera um objeto Comment como resposta.
            Comment persistedComment = restTemplate.postForObject(externalApiUrl, request, Comment.class);
            logger.info("Comentário persistido externamente com sucesso.");
            return persistedComment;
        } catch (RestClientException e) {
            logger.error("ERRO: Falha ao persistir comentário na camada externa na URL '{}'. Causa: {}", externalApiUrl, e.getMessage());
            throw new IllegalStateException("Não foi possível conectar à camada de persistência.", e);
        }
    }

    /**
     * Busca comentários de uma API externa e os persiste localmente.
     */
    public void fetchAndPersistComments() {
        logger.info("Buscando comentários da API externa: {}", externalApiUrl);
        try {
            ExternalCommentDTO[] externalComments = restTemplate.getForObject(externalApiUrl, ExternalCommentDTO[].class);
            if (externalComments != null && externalComments.length > 0) {
                Arrays.stream(externalComments)
                      // Converte o DTO externo para o DTO interno da aplicação.
                      .map(dto -> new CommentRequest(dto.author(), dto.content()))
                      .forEach(this::addComment);
                logger.info("{} comentários externos foram buscados e adicionados com sucesso.", externalComments.length);
            }
        } catch (RestClientException e) {
            logger.error("ERRO: Falha ao buscar e persistir comentários da API externa na URL '{}'. Causa: {}", externalApiUrl, e.getMessage());
        }
    }
}