package hei.school.library.controller.genre;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import hei.school.library.dto.GenreRequest;
import hei.school.library.dto.GenreResponse;
import hei.school.library.endpoint.rest.controller.GenreController;
import hei.school.library.exception.ConflictException;
import hei.school.library.exception.GlobalExceptionHandler;
import hei.school.library.exception.UnprocessableEntityException;
import hei.school.library.service.GenreService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({GenreController.class, GlobalExceptionHandler.class})
public class GenreControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private GenreService genreService;

  @Test
  void should_create_genre() throws Exception {

    UUID id = UUID.randomUUID();

    GenreResponse response = GenreResponse.builder().id(id).name("Fantasy").build();

    when(genreService.createGenreByName(any(GenreRequest.class))).thenReturn(response);

    mockMvc
        .perform(
            post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                            "name":"Fantasy"
                        }
                    """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Fantasy"));
  }

  @Test
  void should_return_conflict_when_genre_already_exists() throws Exception {
    when(genreService.createGenreByName(any(GenreRequest.class)))
        .thenThrow(new ConflictException("The requested resource already exists"));

    mockMvc
        .perform(
            post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                            "name":"Fantasy"
                        }
                    """))
        .andExpect(status().isConflict());
  }

  @Test
  void should_return_unprocessable_entity_exception_when_request_is_not_valid() throws Exception {
    when(genreService.createGenreByName(any(GenreRequest.class)))
        .thenThrow(
            new UnprocessableEntityException(
                "The request body contains invalid JSON or a parameter is malformed"));

    mockMvc
        .perform(
            post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {

                        }
                    """))
        .andExpect(status().isUnprocessableEntity());
  }
}
