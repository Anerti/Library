package hei.school.library.service;

import hei.school.library.dto.BookStockResponse;
import hei.school.library.entity.enums.BookCopyFormat;
import hei.school.library.exception.NotFoundException;
import hei.school.library.repository.dao.AnalyticsRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AnalyticsService {

  private final AnalyticsRepository analyticsRepository;

  public BookStockResponse getStockOverview(UUID libraryId, UUID bookId, String format) {
    if (analyticsRepository.checkBookCopyLink(libraryId, bookId) == null) {
      throw new NotFoundException(String.format(
          "Library %s or Book %s not found", libraryId, bookId));
    }

    BookCopyFormat formatEnum = null;
    BookCopyFormat responseFormat = null;
    if (format != null && !"ALL".equals(format)) {
      formatEnum = BookCopyFormat.valueOf(format);
      responseFormat = formatEnum;
    }

    long total = analyticsRepository.countAvailableStock(bookId, formatEnum, libraryId);

    return BookStockResponse.builder()
        .bookId(bookId)
        .total((int) total)
        .byFormat(responseFormat)
        .build();
  }
}
