package hei.school.library.service;

import hei.school.library.dto.*;
import hei.school.library.entity.enums.BookCopyFormat;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.AnalyticsMapper;
import hei.school.library.projection.RevenueByGenreProjection;
import hei.school.library.repository.dao.AnalyticsRepository;
import hei.school.library.validator.AnalyticsValidator;
import java.time.Instant;
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

  @Transactional(readOnly = true)
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

  @Transactional(readOnly = true)
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
  public RevenueByGenreResponse findRevenueByGenre(
      UUID libraryId, Instant from, Instant to, String sortOrder, int page, int size) {

    analyticsValidator.validateDate(from, to);
    analyticsValidator.validateSortOrder(sortOrder);
    PageRequest pageable = PageRequest.of(page - 1, size);
    Page<RevenueByGenreProjection> found =
        analyticsRepository.findRevenueByGenre(libraryId, from, to, sortOrder, pageable);

    return analyticsMapper.toRevenueByGenreResponse(found, page, size);
  }
}
