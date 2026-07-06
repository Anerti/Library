package hei.school.library.mapper;

import hei.school.library.dto.BookLowStockResponse;
import hei.school.library.dto.BookStockResponse;
import hei.school.library.entity.enums.BookCopyFormat;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsMapper {

  public BookStockResponse toStockResponse(UUID bookId, long total, BookCopyFormat byFormat) {
    return BookStockResponse.builder().bookId(bookId).total(total).byFormat(byFormat).build();
  }

  public BookLowStockResponse toLowStockResponse(Object[] row) {
    return BookLowStockResponse.builder()
        .bookId(UUID.fromString(row[0].toString()))
        .libraryId(UUID.fromString(row[1].toString()))
        .format(row[2] != null ? BookCopyFormat.valueOf(row[2].toString()) : null)
        .stock(((Number) row[3]).longValue())
        .build();
  }
}
