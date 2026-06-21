package hei.school.library.dto;

import java.util.UUID;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GenreResponse {
  private UUID id;
  private String name;
}
