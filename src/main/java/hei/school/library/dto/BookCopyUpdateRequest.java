package hei.school.library.dto;

import hei.school.library.entity.enums.BookCopyFormat;
import hei.school.library.entity.enums.BookCopyStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BookCopyUpdateRequest {
  private Double price;
  private BookCopyFormat format;
  private BookCopyStatus status;
  private Integer pageNumber;
}
