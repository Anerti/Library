package hei.school.library.dto;

import hei.school.library.entity.enums.BookCopyFormat;
import hei.school.library.entity.enums.BookCopyStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class BookCopyResponse {
  private UUID id;
  private Double price;
  private BookCopyFormat format;
  private UUID libraryId;
  private UUID bookId;
  private BookCopyStatus status;
  private Integer pageNumber;
  private LocalDateTime updatedAt;
}
