package hei.school.library.service;

import hei.school.library.dto.BookStockResponse;
import hei.school.library.entity.enums.BookCopyFormat;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.AnalyticsMapper;
import hei.school.library.repository.dao.AnalyticsRepository;
import hei.school.library.validator.AnalyticsValidator;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    String formatParam = responseFormat != null ? responseFormat.name() : null;

    return analyticsMapper.toStockResponse(
        bookId,
        analyticsRepository.countAvailableStock(bookId, formatParam, libraryId),
        responseFormat);
  }
}
