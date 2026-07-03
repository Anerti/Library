package hei.school.library.service.analytics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hei.school.library.dto.PageResponse;
import hei.school.library.dto.RevenueByGenreItem;
import hei.school.library.exception.NotFoundException;
import hei.school.library.repository.dao.AnalyticsRepository;
import hei.school.library.repository.dao.LibraryRepository;
import hei.school.library.service.AnalyticsService;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
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

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

  @Mock private LibraryRepository libraryRepository;
  @Mock private AnalyticsRepository analyticsRepository;
  private AnalyticsService service;
  private UUID libraryId;

  @BeforeEach
  void setUp() {
    libraryId = UUID.randomUUID();
    service = new AnalyticsService(libraryRepository, analyticsRepository);
  }

  @Test
  void should_throw_not_found_when_library_does_not_exist() {
    when(libraryRepository.existsById(libraryId)).thenReturn(false);

    assertThatThrownBy(() -> service.findRevenueByGenre(libraryId, null, null, "desc", 1, 20))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(libraryId.toString());
  }

  @Test
  void should_return_paginated_revenue_when_library_exists() {
    when(libraryRepository.existsById(libraryId)).thenReturn(true);

    RevenueByGenreItem item =
        new RevenueByGenreItem(UUID.randomUUID(), "Fiction", BigDecimal.valueOf(2300.00), 45);
    Page<RevenueByGenreItem> page = new PageImpl<>(List.of(item), PageRequest.of(0, 20), 1);

    when(analyticsRepository.findRevenueByGenre(
            eq(libraryId), any(), any(), eq("desc"), any(Pageable.class)))
        .thenReturn(page);

    PageResponse<RevenueByGenreItem> result =
        service.findRevenueByGenre(libraryId, null, null, "desc", 1, 20);

    assertThat(result.getData()).containsExactly(item);
    assertThat(result.getPagination().getPage()).isEqualTo(1);
    assertThat(result.getPagination().getSize()).isEqualTo(20);
    assertThat(result.getPagination().getTotal()).isEqualTo(1);
  }

  @Test
  void should_default_to_to_now_and_from_to_yesterday_when_dates_are_null() {
    when(libraryRepository.existsById(libraryId)).thenReturn(true);
    when(analyticsRepository.findRevenueByGenre(
            eq(libraryId), any(), any(), eq("desc"), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of()));

    service.findRevenueByGenre(libraryId, null, null, "desc", 1, 20);

    ArgumentCaptor<Instant> startCaptor = ArgumentCaptor.forClass(Instant.class);
    ArgumentCaptor<Instant> endCaptor = ArgumentCaptor.forClass(Instant.class);

    verify(analyticsRepository)
        .findRevenueByGenre(
            eq(libraryId), startCaptor.capture(), endCaptor.capture(), eq("desc"), any());

    Instant capturedStart = startCaptor.getValue();
    Instant capturedEnd = endCaptor.getValue();

    LocalDate today = LocalDate.now();
    Instant expectedEnd = today.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
    Instant expectedStart = today.minusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

    assertThat(capturedEnd).isEqualTo(expectedEnd);
    assertThat(capturedStart).isEqualTo(expectedStart);
  }

  @Test
  void should_use_provided_from_and_to_when_given() {
    when(libraryRepository.existsById(libraryId)).thenReturn(true);
    when(analyticsRepository.findRevenueByGenre(
            eq(libraryId), any(), any(), eq("asc"), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of()));

    LocalDate from = LocalDate.of(2026, 1, 1);
    LocalDate to = LocalDate.of(2026, 1, 31);

    service.findRevenueByGenre(libraryId, from, to, "asc", 1, 20);

    Instant expectedStart = from.atStartOfDay(ZoneOffset.UTC).toInstant();
    Instant expectedEnd = to.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

    verify(analyticsRepository)
        .findRevenueByGenre(libraryId, expectedStart, expectedEnd, "asc", PageRequest.of(0, 20));
  }

  @Test
  void should_convert_page_to_zero_based_pageable() {
    when(libraryRepository.existsById(libraryId)).thenReturn(true);
    when(analyticsRepository.findRevenueByGenre(
            eq(libraryId), any(), any(), eq("desc"), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of()));

    service.findRevenueByGenre(libraryId, null, null, "desc", 3, 10);

    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    verify(analyticsRepository)
        .findRevenueByGenre(eq(libraryId), any(), any(), eq("desc"), pageableCaptor.capture());

    assertThat(pageableCaptor.getValue().getPageNumber()).isEqualTo(2);
    assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(10);
  }
}
