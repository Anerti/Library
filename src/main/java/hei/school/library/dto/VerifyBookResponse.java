package hei.school.library.dto;

import hei.school.library.dto.client.OpenLibraryCover;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerifyBookResponse {
  private Boolean exists;
  private String isbn;
  private String title;
  private List<String> authors;
  private String publisher;
  private String publishDate;
  private Integer numberOfPages;
  private List<String> subjects;
  private OpenLibraryCover cover;
}
