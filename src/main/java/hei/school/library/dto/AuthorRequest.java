package hei.school.library.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
public class AuthorRequest {
  private String firstName;
  private String lastName;
}
