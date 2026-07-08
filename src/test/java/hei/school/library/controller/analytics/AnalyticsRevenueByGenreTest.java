package hei.school.library.controller.analytics;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import hei.school.library.config.JwtTokenProvider;
import hei.school.library.dto.GenreSummary;
import hei.school.library.dto.PaginationDto;
import hei.school.library.dto.RevenueByGenreItem;
import hei.school.library.dto.RevenueByGenreResponse;
import hei.school.library.endpoint.rest.controller.AnalyticsController;
import hei.school.library.exception.GlobalExceptionHandler;
import hei.school.library.exception.NotFoundException;
import hei.school.library.service.AnalyticsService;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({AnalyticsController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
public class AnalyticsRevenueByGenreTest {
  @Autowired private MockMvc mockMvc;
  @MockBean private AnalyticsService service;
  @MockBean private JwtTokenProvider jwtTokenProvider;

  @Test
  void should_return_revenue_by_genre_with_default_params() throws Exception {
    UUID libraryId = UUID.randomUUID();
    GenreSummary genreSummary = new GenreSummary(UUID.randomUUID(), "Fiction");

    RevenueByGenreItem item = new RevenueByGenreItem(genreSummary, BigDecimal.valueOf(2300.00), 45);
    RevenueByGenreResponse response =
        new RevenueByGenreResponse(List.of(item), new PaginationDto(1, 20, 1));

    when(service.findRevenueByGenre(eq(libraryId), any(), any(), eq("desc"), eq(1), eq(20)))
        .thenReturn(response);

    mockMvc
        .perform(get("/libraries/{libraryId}/analytics/revenue/by-genre", libraryId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data", hasSize(1)))
        .andExpect(jsonPath("$.data[0].genre.name").value("Fiction"))
        .andExpect(jsonPath("$.data[0].genre.id").exists())
        .andExpect(jsonPath("$.data[0].totalRevenue").value(2300.00))
        .andExpect(jsonPath("$.data[0].totalSold").value(45))
        .andExpect(jsonPath("$.meta.page").value(1))
        .andExpect(jsonPath("$.meta.size").value(20))
        .andExpect(jsonPath("$.meta.total").value(1));

    verify(service).findRevenueByGenre(libraryId, null, null, "desc", 1, 20);
  }

  @Test
  void should_pass_query_params_to_service() throws Exception {
    UUID libraryId = UUID.randomUUID();

    RevenueByGenreResponse response =
        new RevenueByGenreResponse(List.of(), new PaginationDto(2, 10, 0));

    when(service.findRevenueByGenre(eq(libraryId), any(), any(), eq("asc"), eq(2), eq(10)))
        .thenReturn(response);

    mockMvc
        .perform(
            get("/libraries/{libraryId}/analytics/revenue/by-genre", libraryId)
                .param("from", "2026-01-01T00:00:00Z")
                .param("to", "2026-01-31T23:59:59Z")
                .param("sortOrder", "asc")
                .param("page", "2")
                .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data", hasSize(0)))
        .andExpect(jsonPath("$.meta.page").value(2));

    verify(service)
        .findRevenueByGenre(
            libraryId,
            java.time.Instant.parse("2026-01-01T00:00:00Z"),
            java.time.Instant.parse("2026-01-31T23:59:59Z"),
            "asc",
            2,
            10);
  }

  @Test
  void should_return_not_found_when_library_does_not_exist() throws Exception {
    UUID libraryId = UUID.randomUUID();

    when(service.findRevenueByGenre(eq(libraryId), any(), any(), anyString(), anyInt(), anyInt()))
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
