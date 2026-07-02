package hei.school.library.controller.library;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import hei.school.library.config.JwtTokenProvider;
import hei.school.library.dto.*;
import hei.school.library.endpoint.rest.controller.LibraryController;
import hei.school.library.exception.GlobalExceptionHandler;
import hei.school.library.exception.NotFoundException;
import hei.school.library.service.LibraryService;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({LibraryController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class LibraryControllerTest {

  @Autowired private MockMvc mockMvc;
  @MockBean private LibraryService libraryService;
  @MockBean private JwtTokenProvider jwtTokenProvider;

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
  void should_return_200_when_library_exists() throws Exception {
    UUID id = UUID.randomUUID();
    LibraryResponse library =
        LibraryResponse.builder()
            .id(id)
            .name("Librairie Générale")
            .phone("+261 34 12 345 67")
            .email("contact@librairie-generale.mg")
            .address("15 Avenue de l'Indépendance, Antananarivo")
            .build();

    when(libraryService.getLibrary(id)).thenReturn(library);

    mockMvc
        .perform(get("/libraries/{libraryId}", id).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.name").value("Librairie Générale"))
        .andExpect(jsonPath("$.phone").value("+261 34 12 345 67"))
        .andExpect(jsonPath("$.email").value("contact@librairie-generale.mg"))
        .andExpect(jsonPath("$.address").value("15 Avenue de l'Indépendance, Antananarivo"));
  }

  @Test
  void should_return_404_when_library_not_found() throws Exception {
    UUID id = UUID.randomUUID();
    when(libraryService.getLibrary(id)).thenThrow(new NotFoundException("Library", id));

    mockMvc
        .perform(get("/libraries/{libraryId}", id).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("NOT_FOUND"))
        .andExpect(jsonPath("$.message").value("Library not found with id: " + id));
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

  @Test
  void should_return_revenue_by_genre_with_default_params() throws Exception {
    UUID libraryId = UUID.randomUUID();

    RevenueByGenreItem item =
        new RevenueByGenreItem(UUID.randomUUID(), "Fiction", BigDecimal.valueOf(2300.00), 45);
    PageResponse<RevenueByGenreItem> response =
        new PageResponse<>(List.of(item), new PaginationDto(1, 20, 1));

    when(libraryService.findRevenueByGenre(eq(libraryId), any(), any(), eq("desc"), eq(1), eq(20)))
        .thenReturn(response);

    mockMvc
        .perform(get("/libraries/{libraryId}/analytics/revenue/by-genre", libraryId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data", hasSize(1)))
        .andExpect(jsonPath("$.data[0].genreName").value("Fiction"))
        .andExpect(jsonPath("$.data[0].totalRevenue").value(2300.00))
        .andExpect(jsonPath("$.data[0].totalSold").value(45))
        .andExpect(jsonPath("$.pagination.page").value(1))
        .andExpect(jsonPath("$.pagination.size").value(20))
        .andExpect(jsonPath("$.pagination.total").value(1));

    verify(libraryService).findRevenueByGenre(libraryId, null, null, "desc", 1, 20);
  }

  @Test
  void should_pass_query_params_to_service() throws Exception {
    UUID libraryId = UUID.randomUUID();

    PageResponse<RevenueByGenreItem> response =
        new PageResponse<>(List.of(), new PaginationDto(2, 10, 0));

    when(libraryService.findRevenueByGenre(eq(libraryId), any(), any(), eq("asc"), eq(2), eq(10)))
        .thenReturn(response);

    mockMvc
        .perform(
            get("/libraries/{libraryId}/analytics/revenue/by-genre", libraryId)
                .param("from", "2026-01-01")
                .param("to", "2026-01-31")
                .param("sortOrder", "asc")
                .param("page", "2")
                .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data", hasSize(0)))
        .andExpect(jsonPath("$.pagination.page").value(2));

    verify(libraryService)
        .findRevenueByGenre(
            libraryId,
            java.time.LocalDate.of(2026, 1, 1),
            java.time.LocalDate.of(2026, 1, 31),
            "asc",
            2,
            10);
  }

  @Test
  void should_return_not_found_when_library_does_not_exist() throws Exception {
    UUID libraryId = UUID.randomUUID();

    when(libraryService.findRevenueByGenre(
            eq(libraryId), any(), any(), anyString(), anyInt(), anyInt()))
        .thenThrow(new NotFoundException(String.format("Library '%s' not found", libraryId)));

    mockMvc
        .perform(get("/libraries/{libraryId}/analytics/revenue/by-genre", libraryId))
        .andExpect(status().isNotFound());
  }

  @Test
  void should_return_bad_request_when_library_id_is_invalid_uuid() throws Exception {
    mockMvc
        .perform(get("/libraries/{libraryId}/analytics/revenue/by-genre", "not-a-uuid"))
        .andExpect(status().isBadRequest());
  }
}
