package hei.school.library.dto;

import hei.school.library.entity.enums.BookCopyFormat;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookLowStockResponse {
  private UUID bookId;
  private UUID libraryId;
  private BookCopyFormat format;
  private long stock;
}
