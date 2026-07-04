package hei.school.library.service;

import hei.school.library.dto.BookStockResponse;
import hei.school.library.entity.enums.BookCopyFormat;
import hei.school.library.exception.NotFoundException;
import hei.school.library.repository.dao.AnalyticsRepository;
import hei.school.library.repository.dao.BookRepository;
import hei.school.library.repository.dao.LibraryRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AnalyticsService {

  private final LibraryRepository libraryRepository;
  private final BookRepository bookRepository;
  private final AnalyticsRepository analyticsRepository;

  public BookStockResponse getStockOverview(UUID libraryId, UUID bookId, String format) {
    if (!libraryRepository.existsById(libraryId)) {
      throw new NotFoundException("Library with id " + libraryId + " not found");
    }
    if (!bookRepository.existsById(bookId)) {
      throw new NotFoundException("Book with id " + bookId + " not found");
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
