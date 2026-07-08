package hei.school.library.service.analytics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hei.school.library.dto.GenreSummary;
import hei.school.library.dto.RevenueByGenreResponse;
import hei.school.library.exception.UnprocessableEntityException;
import hei.school.library.mapper.AnalyticsMapper;
import hei.school.library.projection.RevenueByGenreProjection;
import hei.school.library.repository.dao.AnalyticsRepository;
import hei.school.library.service.AnalyticsService;
import hei.school.library.validator.AnalyticsValidator;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;

@ExtendWith(MockitoExtension.class)
public class AnalyticsRevenueByGenreTest {
  @Mock private AnalyticsRepository repository;
  private AnalyticsValidator analyticsValidator;
  private AnalyticsMapper analyticsMapper;
  private AnalyticsService service;
  private UUID libraryId;
  private final ProjectionFactory factory = new SpelAwareProxyProjectionFactory();

  @BeforeEach
  void setUp() {
    libraryId = UUID.randomUUID();
    analyticsMapper = new AnalyticsMapper();
    analyticsValidator = new AnalyticsValidator();
    service = new AnalyticsService(repository, analyticsValidator, analyticsMapper);
  }

  @Test
  void should_throw_when_sort_order_is_invalid() {
    assertThatThrownBy(() -> service.findRevenueByGenre(libraryId, null, null, "invalid", 1, 20))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("Invalid sort order");
  }

  @Test
  void should_throw_when_end_date_before_start_date() {
    Instant from = Instant.parse("2026-02-01T00:00:00Z");
    Instant to = Instant.parse("2026-01-01T00:00:00Z");

    assertThatThrownBy(() -> service.findRevenueByGenre(libraryId, from, to, "DESC", 1, 20))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("Start date must be before end date");
  }

  @Test
  void should_return_paginated_revenue() {
    GenreSummary genreSummary = new GenreSummary(UUID.randomUUID(), "Fiction");
    Map<String, Object> data =
        Map.of(
            "genreId", genreSummary.getId(),
            "genreName", genreSummary.getName(),
            "totalRevenue", BigDecimal.valueOf(2300.00),
            "totalSold", 45);

    RevenueByGenreProjection item = factory.createProjection(RevenueByGenreProjection.class, data);
    Page<RevenueByGenreProjection> page = new PageImpl<>(List.of(item), PageRequest.of(0, 20), 1);

    when(repository.findRevenueByGenre(
            eq(libraryId), any(), any(), eq("DESC"), any(Pageable.class)))
        .thenReturn(page);

    RevenueByGenreResponse result =
        service.findRevenueByGenre(libraryId, null, null, "DESC", 1, 20);

    assertThat(result.getMeta().getPage()).isEqualTo(1);
    assertThat(result.getMeta().getSize()).isEqualTo(20);
    assertThat(result.getMeta().getTotal()).isEqualTo(1);
    assertThat(result.getData()).hasSize(1);
    assertThat(result.getData().get(0).getGenre().getName()).isEqualTo("Fiction");
    assertThat(result.getData().get(0).getTotalRevenue())
        .isEqualByComparingTo(BigDecimal.valueOf(2300.00));
    assertThat(result.getData().get(0).getTotalSold()).isEqualTo(45);
  }

  @Test
  void should_return_null_data_when_no_revenue() {
    when(repository.findRevenueByGenre(
            eq(libraryId), any(), any(), eq("DESC"), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of()));

    RevenueByGenreResponse result =
        service.findRevenueByGenre(libraryId, null, null, "DESC", 1, 20);

    assertThat(result.getData()).isNull();
    assertThat(result.getMeta().getPage()).isEqualTo(1);
    assertThat(result.getMeta().getSize()).isEqualTo(20);
    assertThat(result.getMeta().getTotal()).isZero();
  }

  @Test
  void should_pass_null_dates_when_not_provided() {
    when(repository.findRevenueByGenre(
            eq(libraryId), any(), any(), eq("DESC"), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of()));

    service.findRevenueByGenre(libraryId, null, null, "DESC", 1, 20);

    ArgumentCaptor<Instant> startCaptor = ArgumentCaptor.forClass(Instant.class);
    ArgumentCaptor<Instant> endCaptor = ArgumentCaptor.forClass(Instant.class);

    verify(repository)
        .findRevenueByGenre(
            eq(libraryId), startCaptor.capture(), endCaptor.capture(), eq("DESC"), any());

    assertThat(startCaptor.getValue()).isNull();
    assertThat(endCaptor.getValue()).isNull();
  }

  @Test
  void should_use_provided_from_and_to_when_given() {
    when(repository.findRevenueByGenre(eq(libraryId), any(), any(), eq("ASC"), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of()));

    Instant from = Instant.parse("2026-01-01T00:00:00Z");
    Instant to = Instant.parse("2026-01-31T23:59:59Z");

    service.findRevenueByGenre(libraryId, from, to, "ASC", 1, 20);

    verify(repository).findRevenueByGenre(libraryId, from, to, "ASC", PageRequest.of(0, 20));
  }

  @Test
  void should_convert_page_to_zero_based_pageable() {
    when(repository.findRevenueByGenre(
            eq(libraryId), any(), any(), eq("DESC"), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of()));

    service.findRevenueByGenre(libraryId, null, null, "DESC", 3, 10);

    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    verify(repository)
        .findRevenueByGenre(eq(libraryId), any(), any(), eq("DESC"), pageableCaptor.capture());

    assertThat(pageableCaptor.getValue().getPageNumber()).isEqualTo(2);
    assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(10);
  }
}
