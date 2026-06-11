package hei.school.library.controller.genre;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import hei.school.library.dto.GenreRequest;
import hei.school.library.dto.GenreResponse;
import hei.school.library.endpoint.rest.controller.GenreController;
import hei.school.library.exception.ConflictException;
import hei.school.library.exception.NotFoundException;
import hei.school.library.exception.UnprocessableEntityException;
import hei.school.library.service.GenreService;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class GenreControllerTest {
  private MockMvc mockMvc;
  private GenreService genreService;
  private GenreController genreController;

  @BeforeEach
  void setup() {
    this.genreService = Mockito.mock(GenreService.class);
    this.genreController = new GenreController(this.genreService);
    this.mockMvc = MockMvcBuilders.standaloneSetup(this.genreController).build();
  }

  @Test
  void should_create_genre() throws Exception {

    UUID id = UUID.randomUUID();

    GenreResponse response =
        GenreResponse.builder()
            .id(id)
            .name("Fantasy")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

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

  @Test
  void should_delete_genre_by_id() throws Exception {

    UUID id = UUID.randomUUID();

    GenreResponse response = new GenreResponse();
    response.setId(id);
    response.setName("Fantasy");
    response.setCreatedAt(Instant.now());
    response.setUpdatedAt(Instant.now());

    Mockito.doNothing().when(genreService).deleteGenreById(id);

    mockMvc.perform(delete("/genres/{id}", id)).andExpect(status().isNoContent());
  }

  @Test
  void should_return_not_found_when_genre_id_does_not_exist_on_deleting() throws Exception {
    Mockito.doThrow(new NotFoundException("The requested resource was not found"))
        .when(genreService)
        .deleteGenreById(any(UUID.class));
    UUID id = UUID.randomUUID();

    mockMvc.perform(delete("/genres/{id}", id)).andExpect(status().isNotFound());
  }
}
