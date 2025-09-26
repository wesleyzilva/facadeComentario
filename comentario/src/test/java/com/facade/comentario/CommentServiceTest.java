package com.facade.comentario;

import com.facade.comentario.CommentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommentServiceTest {

    private CommentService commentService;

    @BeforeEach
    void setUp() {
        // Cria uma nova instância do serviço antes de cada teste para garantir isolamento.
        // A implementação atual já vem com 3 comentários iniciais.
        commentService = new CommentService();
               commentService.init(); // Chama o método de inicialização manualmente para o teste unitário.
    }

    @Test
    @DisplayName("Deve inicializar com 3 comentários padrão")
    void shouldInitializeWithDefaultComments() {
        // Act
        List<Comment> comments = commentService.getAllComments();

        // Assert
        assertEquals(3, comments.size());
    }

    @Test
    @DisplayName("Deve adicionar um novo comentário com sucesso")
    void shouldAddComment() {
        // Arrange: Prepara os dados de entrada
        CommentRequest request = new CommentRequest("Test Author", "This is a test comment.");
        int initialSize = commentService.getAllComments().size();

        // Act: Executa a ação a ser testada
        Comment addedComment = commentService.addComment(request);

        // Assert: Verifica os resultados
        assertNotNull(addedComment);
        assertEquals("Test Author", addedComment.author());
        assertEquals("This is a test comment.", addedComment.content());
        assertNotNull(addedComment.id());
        assertEquals(initialSize + 1, commentService.getAllComments().size());
    }
}