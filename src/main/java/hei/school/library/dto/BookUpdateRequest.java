package hei.school.library.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookUpdateRequest {

  private String title;
  private String summary;
  private String isbn;
  private String publisher;
  private LocalDate publishedAt;
}
