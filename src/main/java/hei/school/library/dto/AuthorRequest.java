package hei.school.library.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AuthorRequest {
  private String firstName;
  private String lastName;
}
