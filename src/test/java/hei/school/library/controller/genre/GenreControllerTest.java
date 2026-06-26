package hei.school.library.controller.genre;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import hei.school.library.dto.GenreListResponse;
import hei.school.library.dto.GenreRequest;
import hei.school.library.dto.GenreResponse;
import hei.school.library.endpoint.rest.controller.GenreController;
import hei.school.library.exception.ConflictException;
import hei.school.library.exception.GlobalExceptionHandler;
import hei.school.library.exception.NotFoundException;
import hei.school.library.exception.UnprocessableEntityException;
import hei.school.library.service.GenreService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({GenreController.class, GlobalExceptionHandler.class})
class GenreControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private GenreService genreService;

  private UUID genreId;
  private GenreResponse genreResponse;

  @BeforeEach
  void setUp() {
    genreId = UUID.randomUUID();
    genreResponse = GenreResponse.builder().id(genreId).name("Fantasy").build();
  }

  @Test
  @DisplayName("get /genres/{id}: should return genre when found")
  void should_get_genre_by_id() throws Exception {
    when(genreService.getGenreById(genreId)).thenReturn(genreResponse);

    mockMvc
        .perform(get("/genres/{id}", genreId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(genreId.toString()))
        .andExpect(jsonPath("$.name").value("Fantasy"));

    verify(genreService).getGenreById(genreId);
  }

  @Test
  @DisplayName("get /genres/{id}: should return 404 when not found")
  void should_return_not_found_when_genre_id_does_not_exist() throws Exception {
    when(genreService.getGenreById(any()))
        .thenThrow(new NotFoundException("The requested resource was not found"));

    mockMvc.perform(get("/genres/{id}", UUID.randomUUID())).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("post /genres: should create and return 201")
  void should_create_genre() throws Exception {
    GenreRequest request = new GenreRequest("Fantasy");

    when(genreService.createGenreByName(any(GenreRequest.class))).thenReturn(genreResponse);

    mockMvc
        .perform(
            post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(genreId.toString()))
        .andExpect(jsonPath("$.name").value("Fantasy"));

    verify(genreService).createGenreByName(any(GenreRequest.class));
  }

  @Test
  @DisplayName("post /genres: should return 409 when duplicate name")
  void should_return_conflict_when_genre_already_exists() throws Exception {
    GenreRequest request = new GenreRequest("Fantasy");

    when(genreService.createGenreByName(any(GenreRequest.class)))
        .thenThrow(new ConflictException("The requested resource already exists"));

    mockMvc
        .perform(
            post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict());
  }

  @Test
  @DisplayName("post /genres: should return 422 when request is invalid")
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
  @DisplayName("get /genres: should return paginated list with params")
  void should_get_all_genres_with_pagination() throws Exception {
    String search = "Fantasy";
    int page = 1;
    int size = 20;
    GenreListResponse mockResponse = new GenreListResponse();

    when(genreService.findAll(search, page, size)).thenReturn(mockResponse);

    mockMvc
        .perform(
            get("/genres")
                .param("search", search)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size)))
        .andExpect(status().isOk());

    verify(genreService).findAll(search, page, size);
  }

  @Test
  @DisplayName("get /genres: should use defaults when no params")
  void should_use_default_pagination_parameters_when_not_provided() throws Exception {
    GenreListResponse mockResponse = new GenreListResponse();

    when(genreService.findAll(null, 1, 20)).thenReturn(mockResponse);

    mockMvc.perform(get("/genres")).andExpect(status().isOk());

    verify(genreService).findAll(null, 1, 20);
  }

  @Test
  @DisplayName("delete /genres/{id}: should return 204")
  void should_delete_genre_by_id() throws Exception {
    doNothing().when(genreService).deleteGenreById(genreId);

    mockMvc.perform(delete("/genres/{id}", genreId)).andExpect(status().isNoContent());

    verify(genreService).deleteGenreById(genreId);
  }

  @Test
  @DisplayName("delete /genres/{id}: should return 404 when not found")
  void should_return_not_found_when_genre_id_does_not_exist_on_deleting() throws Exception {
    doThrow(new NotFoundException("The requested resource was not found"))
        .when(genreService)
        .deleteGenreById(any(UUID.class));

    mockMvc.perform(delete("/genres/{id}", UUID.randomUUID())).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("patch /genres/{id}: should update and return 200")
  void should_update_genre_by_name() throws Exception {
    when(genreService.updateGenreByName(any(UUID.class), any(GenreRequest.class)))
        .thenReturn(genreResponse);

    mockMvc
        .perform(
            patch("/genres/{id}", genreId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new GenreRequest("Fantasy"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(genreId.toString()))
        .andExpect(jsonPath("$.name").value("Fantasy"));

    verify(genreService).updateGenreByName(any(UUID.class), any(GenreRequest.class));
  }

  @Test
  @DisplayName("patch /genres/{id}: should return 404 when not found")
  void should_return_not_found_when_genre_id_does_not_exist_on_updating() throws Exception {
    when(genreService.updateGenreByName(any(UUID.class), any(GenreRequest.class)))
        .thenThrow(new NotFoundException("Genre with id " + genreId + " not found"));

    mockMvc
        .perform(
            patch("/genres/{id}", genreId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new GenreRequest("Action"))))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("patch /genres/{id}: should return 409 on duplicate name")
  void should_return_conflict_when_name_already_exists() throws Exception {
    when(genreService.updateGenreByName(any(UUID.class), any(GenreRequest.class)))
        .thenThrow(new ConflictException("Genre Fantasy already exists."));

    mockMvc
        .perform(
            patch("/genres/{id}", genreId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new GenreRequest("Fantasy"))))
        .andExpect(status().isConflict());
  }
}
