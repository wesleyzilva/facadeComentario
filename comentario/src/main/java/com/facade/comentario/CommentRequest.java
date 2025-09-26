package com.facade.comentario;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para receber os dados de criação de um novo comentário.
 * Inclui apenas os campos que o cliente deve fornecer.
 */
public record CommentRequest(@NotBlank String author, @NotBlank String content) {
}