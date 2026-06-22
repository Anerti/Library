package hei.school.library.controller.library;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import hei.school.library.dto.LibraryRequest;
import hei.school.library.dto.LibraryResponse;
import hei.school.library.endpoint.rest.controller.LibraryController;
import hei.school.library.exception.ConflictException;
import hei.school.library.exception.GlobalExceptionHandler;
import hei.school.library.exception.NotFoundException;
import hei.school.library.exception.UnprocessableEntityException;
import hei.school.library.service.LibraryService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({LibraryController.class, GlobalExceptionHandler.class})
class LibraryControllerTest {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockBean private LibraryService libraryService;

  @BeforeEach
  void setup() {}

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

  @Test
  void getLibraryById_ShouldReturnOkAndResponse_WhenLibraryExists() throws Exception {
    UUID libraryId = UUID.randomUUID();
    LibraryResponse response =
        LibraryResponse.builder()
            .id(libraryId)
            .name("Bibliothèque Centrale")
            .email("contact@biblio.com")
            .build();

    when(libraryService.getLibraryById(libraryId)).thenReturn(response);
    mockMvc
        .perform(get("/libraries/" + libraryId).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(libraryId.toString()))
        .andExpect(jsonPath("$.name").value("Bibliothèque Centrale"))
        .andExpect(jsonPath("$.email").value("contact@biblio.com"));
  }

  @Test
  void getLibraryById_ShouldReturnNotFound_WhenLibraryDoesNotExist() throws Exception {
    UUID libraryId = UUID.randomUUID();
    when(libraryService.getLibraryById(libraryId))
        .thenThrow(new NotFoundException("The requested resource was not found"));

    mockMvc
        .perform(get("/libraries/" + libraryId).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }
}
