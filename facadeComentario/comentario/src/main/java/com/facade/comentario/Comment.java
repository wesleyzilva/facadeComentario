package com.facade.comentario.model;

import java.time.Instant;

/**
 * Representa um comentário. Usar um record é ideal para objetos de dados imutáveis.
 * O compilador Java gera automaticamente o construtor, getters, equals(), hashCode() e toString().
 */
public record Comment(Long id, String author, String content, Instant timestamp) {
}