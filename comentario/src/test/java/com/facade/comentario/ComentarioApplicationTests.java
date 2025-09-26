package com.facade.comentario;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ComentarioApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	void shouldReturnListOfComments() throws Exception {
		mockMvc.perform(get("/api/comments"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].author").value("Alice"))
				.andExpect(jsonPath("$.length()").value(3));
	}

	@Test
	void shouldCreateComment() throws Exception {
		String commentJson = """
        {
            "author": "Test User",
            "content": "A new comment from test"
        }
        """;

		mockMvc.perform(post("/api/comments").contentType(MediaType.APPLICATION_JSON).content(commentJson))
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

		mockMvc.perform(post("/api/comments").contentType(MediaType.APPLICATION_JSON).content(commentJson))
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

		mockMvc.perform(post("/api/comments").contentType(MediaType.APPLICATION_JSON).content(commentJson))
				.andExpect(status().isBadRequest());
	}

}
