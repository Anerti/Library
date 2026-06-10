package hei.school.library.dto;

import hei.school.library.entity.enums.BookCopyFormat;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BookCopyRequest {
  private Double price;
  private BookCopyFormat format;
  private UUID bookId;
  private Integer pageNumber;
}
