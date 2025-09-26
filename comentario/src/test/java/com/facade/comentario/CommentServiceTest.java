package com.facade.comentario;

import com.facade.comentario.CommentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    private CommentService commentService;
    @Mock
    private RestTemplate restTemplate;
    private final String testApiUrl = "http://test.url/api";

    @BeforeEach
    void setUp() {
        // Cria uma nova instância do serviço antes de cada teste para garantir isolamento.
        commentService = new CommentService(restTemplate);
        // Injeta a URL de teste no campo 'externalApiUrl' do serviço
        ReflectionTestUtils.setField(commentService, "externalApiUrl", testApiUrl);
    }

    @Test
    @DisplayName("Deve buscar e retornar uma lista de comentários da API externa")
    void shouldFetchAndReturnComments() {
        // Arrange: Simula a resposta da API externa
        Comment[] mockComments = {
                new Comment(1L, "External Alice", "Post 1", Instant.now()),
                new Comment(2L, "External Bob", "Post 2", Instant.now())
        };
        when(restTemplate.getForObject(testApiUrl, Comment[].class)).thenReturn(mockComments);

        // Act
        List<Comment> comments = commentService.getAllComments();

        // Assert
        assertNotNull(comments);
        assertEquals(2, comments.size());
        assertEquals("External Alice", comments.get(0).author());
        verify(restTemplate, times(1)).getForObject(testApiUrl, Comment[].class);
    }

    @Test
    @DisplayName("Deve enviar um novo comentário para a API externa e retornar o resultado")
    void shouldAddComment() {
        // Arrange: Prepara os dados de entrada
        CommentRequest request = new CommentRequest("Test Author", "This is a test comment.");
        Comment mockResponse = new Comment(10L, request.author(), request.content(), Instant.now());
        when(restTemplate.postForObject(testApiUrl, request, Comment.class)).thenReturn(mockResponse);

        // Act: Executa a ação a ser testada
        Comment addedComment = commentService.addComment(request);

        // Assert: Verifica os resultados
        assertNotNull(addedComment);
        assertEquals(10L, addedComment.id());
        assertEquals("Test Author", addedComment.author());
        verify(restTemplate, times(1)).postForObject(testApiUrl, request, Comment.class);
    }
}