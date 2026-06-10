package hei.school.library.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookResponse {
  private UUID id;
  private String title;
  private String summary;
  private String isbn;
  private String publisher;
  private LocalDate publishedAt;
  private LocalDateTime createdAt;
  private List<AuthorResponse> authors;
  private List<GenreSummary> genres;
}
