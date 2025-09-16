package com.facade.comentario.model;

/**
 * DTO para receber os dados de criação de um novo comentário.
 * Inclui apenas os campos que o cliente deve fornecer.
 */
public record CommentRequest(String author, String content) {
}