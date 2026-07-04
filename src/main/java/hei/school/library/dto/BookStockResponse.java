package hei.school.library.dto;

import hei.school.library.entity.enums.BookCopyFormat;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class BookStockResponse {
  private UUID bookId;
  private long total;
  private BookCopyFormat byFormat;
}
