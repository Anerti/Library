package hei.school.library.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LibraryRequest {
  private String name;
  private String phone;
  private String email;
  private String address;
}
