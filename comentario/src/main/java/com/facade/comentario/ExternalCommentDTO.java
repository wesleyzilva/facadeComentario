package com.facade.comentario;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO (Data Transfer Object) para mapear a resposta da API externa.
 * A anotação @JsonIgnoreProperties(ignoreUnknown = true) torna a desserialização
 * mais flexível, ignorando quaisquer campos no JSON que não estejam definidos neste record.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ExternalCommentDTO(String author, String content) {
}

