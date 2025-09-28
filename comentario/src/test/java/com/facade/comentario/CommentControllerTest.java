package com.facade.comentario;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    @Test
    void getAllComments_shouldReturnListOfComments() throws Exception {
        // Arrange
        List<Comment> comments = List.of(
            new Comment(1L, "Test Author", "Test Content", Instant.now())
        );
        when(commentService.getAllComments()).thenReturn(comments);

        // Act & Assert
        mockMvc.perform(get("/api/facadeComments"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].author").value("Test Author"));
    }

    @Test
    void getAllComments_shouldReturnEmptyList_whenNoCommentsExist() throws Exception {
        // Arrange
        when(commentService.getAllComments()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/facadeComments"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void createComment_shouldReturn201Created_withValidRequest() throws Exception {
        // Arrange
        CommentRequest request = new CommentRequest("New Author", "Valid content");
        Comment createdComment = new Comment(1L, "New Author", "Valid content", Instant.now());

        when(commentService.addComment(any(CommentRequest.class))).thenReturn(createdComment);

        // Act & Assert
        mockMvc.perform(post("/api/facadeComments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.author").value("New Author"));
    }

    @Test
    void createComment_shouldReturn400BadRequest_withInvalidRequest() throws Exception {
        // Arrange: author is blank, which violates @NotBlank
        CommentRequest invalidRequest = new CommentRequest("", "Some content");

        // Act & Assert
        mockMvc.perform(post("/api/facadeComments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void createComment_shouldReturn503ServiceUnavailable_whenServiceFails() throws Exception {
        // Arrange
        CommentRequest request = new CommentRequest("Author", "Content");
        when(commentService.addComment(any(CommentRequest.class)))
            .thenThrow(new ExternalServiceException("Service is down"));

        // Act & Assert
        mockMvc.perform(post("/api/facadeComments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isServiceUnavailable());
    }
}