package hei.school.library.dto.client;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenLibraryCover {

  private String small;
  private String medium;
  private String large;
}
