package com.facade.comentario;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    @Value("${external.api.comments.url}")
    private String externalApiUrl;

    private final RestTemplate restTemplate;

    public CommentService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Busca todos os comentários delegando a chamada para a camada de persistência.
     * @return Uma lista de comentários ou uma lista vazia em caso de falha.
     */
    public List<Comment> getAllComments() {
        logger.info("Buscando todos os comentários da camada de persistência: {}", externalApiUrl);
        try {
            Comment[] externalComments = restTemplate.getForObject(externalApiUrl, Comment[].class);
            if (externalComments != null) {
                return Arrays.asList(externalComments);
            }
            // Se a resposta for nula (mas sem exceção), retorna lista vazia.
            return Collections.emptyList();
        } catch (RestClientException e) {
            logger.error("ERRO: Falha ao buscar comentários da camada de persistência na URL '{}'. Causa: {}", externalApiUrl, e.getMessage());
            // Em um cenário real, você poderia lançar uma exceção customizada aqui.
            return Collections.emptyList(); // Retorna uma lista vazia imutável em caso de falha.
        }
    }

    /**
     * Envia um novo comentário para ser criado na camada de persistência.
     * @param request O DTO com os dados do comentário.
     * @return O comentário criado, conforme retornado pela camada de persistência.
     * @throws ExternalServiceException se a comunicação com o serviço externo falhar.
     */
    public Comment addComment(CommentRequest request) {
        logger.info("Enviando novo comentário para a camada de persistência: {}", externalApiUrl);
        try {
            // Envia o DTO para a API externa e espera um objeto Comment como resposta.
            Comment persistedComment = restTemplate.postForObject(externalApiUrl, request, Comment.class);
            if (persistedComment == null) {
                // O serviço respondeu 2xx mas com corpo vazio, o que não é esperado.
                throw new ExternalServiceException("A camada de persistência retornou uma resposta vazia inesperada.");
            }
            logger.info("Comentário persistido externamente com sucesso: {}", persistedComment);
            return persistedComment;
        } catch (RestClientException e) {
            logger.error("ERRO: Falha ao persistir comentário na camada externa na URL '{}'. Causa: {}", externalApiUrl, e.getMessage());
            // Lança uma exceção de domínio específica para melhor tratamento no Controller.
            throw new ExternalServiceException("Não foi possível conectar à camada de persistência.", e);
        }
    }
}