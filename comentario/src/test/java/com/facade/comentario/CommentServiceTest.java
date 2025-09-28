package com.facade.comentario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CommentService commentService;

    private final String testApiUrl = "http://test-api/comments";

    @BeforeEach
    void setUp() {
        // Injeta o valor da URL da API externa para os testes
        ReflectionTestUtils.setField(commentService, "externalApiUrl", testApiUrl);
    }

    @Test
    void getAllComments_shouldReturnComments_whenApiSucceeds() {
        // Arrange
        Comment[] expectedComments = {
            new Comment(1L, "Author 1", "Content 1", Instant.now()),
            new Comment(2L, "Author 2", "Content 2", Instant.now())
        };
        when(restTemplate.getForObject(testApiUrl, Comment[].class)).thenReturn(expectedComments);

        // Act
        List<Comment> actualComments = commentService.getAllComments();

        // Assert
        assertThat(actualComments).hasSize(2);
        assertThat(actualComments.get(0).author()).isEqualTo("Author 1");
        verify(restTemplate).getForObject(testApiUrl, Comment[].class);
    }

    @Test
    void getAllComments_shouldReturnEmptyList_whenApiFails() {
        // Arrange
        when(restTemplate.getForObject(testApiUrl, Comment[].class)).thenThrow(new RestClientException("API is down"));

        // Act
        List<Comment> actualComments = commentService.getAllComments();

        // Assert
        assertThat(actualComments).isEmpty();
    }

    @Test
    void addComment_shouldReturnPersistedComment_whenApiSucceeds() {
        // Arrange
        CommentRequest request = new CommentRequest("New Author", "New Content");
        Comment expectedResponse = new Comment(3L, "New Author", "New Content", Instant.now());

        when(restTemplate.postForObject(testApiUrl, request, Comment.class)).thenReturn(expectedResponse);

        // Act
        Comment actualResponse = commentService.addComment(request);

        // Assert
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.id()).isEqualTo(3L);
        assertThat(actualResponse.author()).isEqualTo("New Author");
        verify(restTemplate).postForObject(testApiUrl, request, Comment.class);
    }

    @Test
    void addComment_shouldThrowExternalServiceException_whenApiFails() {
        // Arrange
        CommentRequest request = new CommentRequest("Author", "Content");
        when(restTemplate.postForObject(testApiUrl, request, Comment.class))
            .thenThrow(new RestClientException("Connection refused"));

        // Act & Assert
        ExternalServiceException exception = assertThrows(ExternalServiceException.class, () -> {
            commentService.addComment(request);
        });

        assertThat(exception.getMessage()).isEqualTo("Não foi possível conectar à camada de persistência.");
        assertThat(exception.getCause()).isInstanceOf(RestClientException.class);
    }

    @Test
    void addComment_shouldThrowExternalServiceException_whenApiReturnsNull() {
        // Arrange
        CommentRequest request = new CommentRequest("Author", "Content");
        when(restTemplate.postForObject(testApiUrl, request, Comment.class)).thenReturn(null);

        // Act & Assert
        ExternalServiceException exception = assertThrows(ExternalServiceException.class, () -> {
            commentService.addComment(request);
        });

        assertThat(exception.getMessage()).isEqualTo("A camada de persistência retornou uma resposta vazia inesperada.");
    }
}