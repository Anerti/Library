package hei.school.library.controller.library;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import hei.school.library.dto.LibraryListResponse;
import hei.school.library.dto.LibraryResponse;
import hei.school.library.dto.PaginationDto;
import hei.school.library.endpoint.rest.controller.LibraryController;
import hei.school.library.exception.GlobalExceptionHandler;
import hei.school.library.exception.NotFoundException;
import hei.school.library.service.LibraryService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({LibraryController.class, GlobalExceptionHandler.class})
class LibraryControllerTest {

  @Autowired private MockMvc mockMvc;
  @MockBean private LibraryService libraryService;

  @Test
  void should_return_200_with_list_of_libraries() throws Exception {
    LibraryResponse library =
        LibraryResponse.builder()
            .id(UUID.randomUUID())
            .name("Librairie Générale")
            .phone("+261 34 12 345 67")
            .email("contact@librairie-generale.mg")
            .address("15 Avenue de l'Indépendance, Antananarivo")
            .build();

    PaginationDto meta = PaginationDto.builder().page(1).size(20).total(1).build();

    LibraryListResponse response =
        LibraryListResponse.builder().data(List.of(library)).meta(meta).build();

    when(libraryService.listLibraries(null, 1, 20)).thenReturn(response);

    mockMvc
        .perform(get("/libraries").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data[0].name").value("Librairie Générale"))
        .andExpect(jsonPath("$.meta.page").value(1))
        .andExpect(jsonPath("$.meta.size").value(20))
        .andExpect(jsonPath("$.meta.total").value(1));
  }

  @Test
  void should_filter_libraries_by_search_query() throws Exception {
    LibraryResponse library =
        LibraryResponse.builder()
            .id(UUID.randomUUID())
            .name("Tech Library")
            .phone("+261 34 12 345 67")
            .email("tech@library.com")
            .address("456 Avenue")
            .build();

    PaginationDto meta = PaginationDto.builder().page(1).size(20).total(1).build();

    LibraryListResponse response =
        LibraryListResponse.builder().data(List.of(library)).meta(meta).build();

    when(libraryService.listLibraries("Tech", 1, 20)).thenReturn(response);

    mockMvc
        .perform(get("/libraries").param("search", "Tech").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data[0].name").value("Tech Library"))
        .andExpect(jsonPath("$.meta.total").value(1));
  }

  @Test
  void should_respect_pagination_parameters() throws Exception {
    LibraryResponse library =
        LibraryResponse.builder()
            .id(UUID.randomUUID())
            .name("Lib A")
            .phone("+261 00 00 00 00")
            .email("a@lib.com")
            .address("Addr")
            .build();

    PaginationDto meta = PaginationDto.builder().page(2).size(10).total(1).build();

    LibraryListResponse response =
        LibraryListResponse.builder().data(List.of(library)).meta(meta).build();

    when(libraryService.listLibraries(null, 2, 10)).thenReturn(response);

    mockMvc
        .perform(
            get("/libraries")
                .param("page", "2")
                .param("size", "10")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.page").value(2))
        .andExpect(jsonPath("$.meta.size").value(10));
  }

  @Test
  void should_return_empty_list_when_no_libraries() throws Exception {
    PaginationDto meta = PaginationDto.builder().page(1).size(20).total(0).build();

    LibraryListResponse response = LibraryListResponse.builder().data(List.of()).meta(meta).build();

    when(libraryService.listLibraries("nonexistent", 1, 20)).thenReturn(response);

    mockMvc
        .perform(
            get("/libraries").param("search", "nonexistent").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isEmpty())
        .andExpect(jsonPath("$.meta.total").value(0));
  }

  @Test
  void should_return_204_when_deleting_existing_library() throws Exception {
    UUID id = UUID.randomUUID();

    mockMvc
        .perform(delete("/libraries/{libraryId}", id).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }

  @Test
  void should_return_404_when_deleting_non_existent_library() throws Exception {
    UUID id = UUID.randomUUID();
    doThrow(new NotFoundException("Library", id)).when(libraryService).deleteLibrary(id);

    mockMvc
        .perform(delete("/libraries/{libraryId}", id).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("NOT_FOUND"))
        .andExpect(jsonPath("$.message").value("Library not found with id: " + id));
  }
}
