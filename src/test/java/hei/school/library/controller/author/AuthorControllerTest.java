package hei.school.library.controller.author;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import hei.school.library.dto.AuthorListResponse;
import hei.school.library.dto.AuthorRequest;
import hei.school.library.dto.AuthorResponse;
import hei.school.library.dto.AuthorUpdateRequest;
import hei.school.library.endpoint.rest.controller.AuthorController;
import hei.school.library.exception.GlobalExceptionHandler;
import hei.school.library.exception.NotFoundException;
import hei.school.library.service.AuthorService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({AuthorController.class, GlobalExceptionHandler.class})
public class AuthorControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private AuthorService authorService;

  private UUID authorId;
  private AuthorResponse authorResponse;

  @BeforeEach
  void setUp() {
    authorId = UUID.randomUUID();

    authorResponse =
        AuthorResponse.builder().id(authorId).firstName("Albert").lastName("Camus").build();
  }

  @Test
  void should_get_all_authors_with_pagination() throws Exception {
    String search = "Camus";
    AuthorListResponse mockPageResponse = new AuthorListResponse();

    when(authorService.findAll(search, 1, 20)).thenReturn(mockPageResponse);

    mockMvc
        .perform(get("/authors").param("search", search).param("page", "1").param("size", "20"))
        .andExpect(status().isOk());

    verify(authorService).findAll(search, 1, 20);
  }

  @Test
  void should_use_default_pagination_when_not_provided() throws Exception {
    AuthorListResponse mockPageResponse = new AuthorListResponse();

    when(authorService.findAll(null, 1, 20)).thenReturn(mockPageResponse);

    mockMvc.perform(get("/authors")).andExpect(status().isOk());

    verify(authorService).findAll(null, 1, 20);
  }

  @Test
  void should_get_author_by_id() throws Exception {
    when(authorService.findById(authorId)).thenReturn(authorResponse);

    mockMvc
        .perform(get("/authors/{id}", authorId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(authorId.toString()))
        .andExpect(jsonPath("$.firstName").value("Albert"))
        .andExpect(jsonPath("$.lastName").value("Camus"));
  }

  @Test
  void should_return_not_found_when_author_does_not_exist() throws Exception {
    when(authorService.findById(authorId))
        .thenThrow(new NotFoundException("Author with id " + authorId + " not found"));

    mockMvc.perform(get("/authors/{id}", authorId)).andExpect(status().isNotFound());
  }

  @Test
  void should_create_author() throws Exception {
    AuthorRequest request = new AuthorRequest("Albert", "Camus");

    when(authorService.create(any(AuthorRequest.class))).thenReturn(authorResponse);

    mockMvc
        .perform(
            post("/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(authorId.toString()))
        .andExpect(jsonPath("$.firstName").value("Albert"))
        .andExpect(jsonPath("$.lastName").value("Camus"));
  }

  @Test
  void should_update_author() throws Exception {
    AuthorUpdateRequest request = new AuthorUpdateRequest("Albert", "Camus Updated");

    AuthorResponse updatedResponse =
        AuthorResponse.builder().id(authorId).firstName("Albert").lastName("Camus Updated").build();

    when(authorService.update(eq(authorId), any(AuthorUpdateRequest.class)))
        .thenReturn(updatedResponse);

    mockMvc
        .perform(
            patch("/authors/{id}", authorId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.lastName").value("Camus Updated"));
  }

  @Test
  void should_return_not_found_when_author_does_not_exist_on_update() throws Exception {
    AuthorUpdateRequest request = new AuthorUpdateRequest("Albert", "Camus Updated");

    when(authorService.update(eq(authorId), any(AuthorUpdateRequest.class)))
        .thenThrow(new NotFoundException("Author with id " + authorId + " not found"));

    mockMvc
        .perform(
            patch("/authors/{id}", authorId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  void should_delete_author() throws Exception {
    doNothing().when(authorService).delete(authorId);

    mockMvc.perform(delete("/authors/{id}", authorId)).andExpect(status().isNoContent());

    verify(authorService).delete(authorId);
  }

  @Test
  void should_return_not_found_when_author_does_not_exist_on_delete() throws Exception {
    doThrow(new NotFoundException("Author with id " + authorId + " not found"))
        .when(authorService)
        .delete(authorId);

    mockMvc.perform(delete("/authors/{id}", authorId)).andExpect(status().isNotFound());
  }
}
