package hei.school.library.controller.library;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import hei.school.library.dto.LibraryRequest;
import hei.school.library.dto.LibraryResponse;
import hei.school.library.endpoint.rest.controller.LibraryController;
import hei.school.library.exception.ConflictException;
import hei.school.library.exception.UnprocessableEntityException;
import hei.school.library.service.LibraryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

class LibraryControllerTest {

  private MockMvc mockMvc;
  private ObjectMapper objectMapper = new ObjectMapper();
  private LibraryController libraryController;

  private LibraryService libraryService;

  @BeforeEach
  void setup() {
    this.libraryService = Mockito.mock(LibraryService.class);
    this.libraryController = new LibraryController(this.libraryService);
    this.mockMvc = MockMvcBuilders.standaloneSetup(this.libraryController).build();
  }

  @Test
  void should_create_library() throws Exception {
    LibraryRequest request = new LibraryRequest();
    request.setName("Central Library");
    request.setEmail("contact@central.com");

    LibraryResponse response = new LibraryResponse();

    when(libraryService.createLibrary(any(LibraryRequest.class))).thenReturn(response);
    mockMvc
        .perform(
            post("/libraries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());
  }

  @Test
  void should_throw_conflict_exception() throws Exception {
    LibraryRequest request = new LibraryRequest();
    request.setName("Central Library");
    request.setEmail("contact@central.com");
    String errorMessage = "Library with email contact@central.com already exists";
    when(libraryService.createLibrary(any(LibraryRequest.class)))
        .thenThrow(new ConflictException(errorMessage));
    mockMvc
        .perform(
            post("/libraries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict());
  }

  @Test
  void create_should_return_422_unprocessable_entity_when_service_validation_fails()
      throws Exception {
    LibraryRequest invalidRequest = new LibraryRequest();
    invalidRequest.setName("Librairie Invalide");
    when(libraryService.createLibrary(any(LibraryRequest.class)))
        .thenThrow(new UnprocessableEntityException("Invalid phone format: 'abc'."));
    mockMvc
        .perform(
            post("/libraries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isUnprocessableEntity());
  }
}
