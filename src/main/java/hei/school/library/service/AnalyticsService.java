package hei.school.library.service;

import hei.school.library.dto.*;
import hei.school.library.entity.enums.BookCopyFormat;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.AnalyticsMapper;
import hei.school.library.projection.RevenueByGenreProjection;
import hei.school.library.repository.dao.AnalyticsRepository;
import hei.school.library.validator.AnalyticsValidator;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AnalyticsService {

  private final AnalyticsRepository analyticsRepository;
  private final AnalyticsValidator analyticsValidator;
  private final AnalyticsMapper analyticsMapper;

  public BookStockResponse getStockOverview(UUID libraryId, UUID bookId, String format) {
    analyticsValidator.validateFormat(format);

    if (analyticsRepository.checkBookCopyLink(libraryId, bookId) == null) {
      throw new NotFoundException(
          String.format("Library %s or Book %s not found", libraryId, bookId));
    }

    BookCopyFormat responseFormat = !"ALL".equals(format) ? BookCopyFormat.valueOf(format) : null;

    return analyticsMapper.toStockResponse(
        bookId,
        analyticsRepository.countAvailableStock(
            bookId, responseFormat != null ? responseFormat.name() : null, libraryId),
        responseFormat);
  }

  public List<BookLowStockResponse> getLowStockBooks(
      UUID libraryId, UUID bookId, int threshold, String format) {
    analyticsValidator.validateThreshold(threshold);
    analyticsValidator.validateFormat(format);

    if (analyticsRepository.checkBookCopyLink(libraryId, bookId) == null) {
      throw new NotFoundException(
          String.format("Library %s or Book %s not found", libraryId, bookId));
    }

    return analyticsRepository.findLowStockBooks(libraryId, bookId, threshold, format).stream()
        .map(analyticsMapper::toLowStockResponse)
        .toList();
  }

  @Transactional(readOnly = true)
  public PageResponse findRevenueByGenre(
      UUID libraryId, LocalDate from, LocalDate to, String sortOrder, int page, int size) {

    if (!analyticsRepository.existsLibraryById(libraryId)) {
      throw new NotFoundException(String.format("Library '%s' not found", libraryId));
    }

    LocalDate resolvedTo = (to != null) ? to : LocalDate.now();
    LocalDate resolvedFrom = (from != null) ? from : resolvedTo.minusDays(1);

    Instant start = resolvedFrom.atStartOfDay(ZoneOffset.UTC).toInstant();
    Instant end = resolvedTo.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

    PageRequest pageable = PageRequest.of(page - 1, size);
    Page<RevenueByGenreProjection> found =
        analyticsRepository.findRevenueByGenre(libraryId, start, end, sortOrder, pageable);
    Page<RevenueByGenreItem> result =
        found.map(
            p ->
                new RevenueByGenreItem(
                    new GenreSummary(p.getGenreId(), p.getGenreName()),
                    p.getTotalRevenue(),
                    p.getTotalSold()));

    return new PageResponse(
        result.getContent(), new PaginationDto(page, size, result.getTotalElements()));
  }
}
