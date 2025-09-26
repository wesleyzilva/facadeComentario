package com.facade.comentario;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ComentarioApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private RestTemplate restTemplate;

	@Test
	void contextLoads() {
	}

	@Test
	void shouldReturnListOfComments() throws Exception {
		// Arrange: Simula a resposta da API externa
		Comment[] mockComments = {
				new Comment(1L, "External Alice", "External Post", Instant.now()),
				new Comment(2L, "External Bob", "Another Post", Instant.now())
		};
		when(restTemplate.getForObject("http://localhost:8082/api/persistComments", Comment[].class))
				.thenReturn(mockComments);

		// Act & Assert
		mockMvc.perform(get("/api/facadeComments"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].author").value("External Alice"))
				.andExpect(jsonPath("$[1].author").value("External Bob"))
				.andExpect(jsonPath("$.length()").value(2));
	}

	@Test
	void shouldCreateComment() throws Exception {
		// Arrange: Prepara o corpo da requisição e simula a resposta da API externa
		String commentJson = """
        {
            "author": "Test User",
            "content": "A new comment from test"
        }
        """;
		CommentRequest request = new CommentRequest("Test User", "A new comment from test");
		Comment mockResponse = new Comment(10L, request.author(), request.content(), Instant.now());

		when(restTemplate.postForObject(any(String.class), any(CommentRequest.class), any(Class.class)))
				.thenReturn(mockResponse);

		// Act & Assert
		mockMvc.perform(post("/api/facadeComments").contentType(MediaType.APPLICATION_JSON).content(commentJson))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.author").value("Test User"))
				.andExpect(jsonPath("$.content").value("A new comment from test"));
	}

	@Test
	void shouldFailToCreateCommentWhenAuthorIsBlank() throws Exception {
		String commentJson = """
        {
            "author": "",
            "content": "Content without author"
        }
        """;

		mockMvc.perform(post("/api/facadeComments").contentType(MediaType.APPLICATION_JSON).content(commentJson))
				.andExpect(status().isBadRequest());
	}

	@Test
	void shouldFailToCreateCommentWhenContentIsBlank() throws Exception {
		String commentJson = """
        {
            "author": "Author without content",
            "content": ""
        }
        """;

		mockMvc.perform(post("/api/facadeComments").contentType(MediaType.APPLICATION_JSON).content(commentJson))
				.andExpect(status().isBadRequest());
	}

}
