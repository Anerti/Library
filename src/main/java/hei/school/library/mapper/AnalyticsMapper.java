package hei.school.library.mapper;

import hei.school.library.dto.BookStockResponse;
import hei.school.library.entity.enums.BookCopyFormat;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsMapper {

  public BookStockResponse toStockResponse(UUID bookId, long total, BookCopyFormat byFormat) {
    return BookStockResponse.builder().bookId(bookId).total(total).byFormat(byFormat).build();
  }
}
