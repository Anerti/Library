package hei.school.library.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LibraryResponse {

  private UUID id;
  private String name;
  private String phone;
  private String email;
  private String address;
}
