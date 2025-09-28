package com.facade.comentario;

/**
 * Exceção customizada para representar erros que ocorrem durante a comunicação
 * com serviços externos, como a camada de persistência.
 */
public class ExternalServiceException extends RuntimeException {
    public ExternalServiceException(String message) {
        super(message);
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}