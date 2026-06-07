package hei.school.library.dto;

import java.util.UUID;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class AuthorResponse {
  private UUID id;
  private String firstName;
  private String lastName;
}
