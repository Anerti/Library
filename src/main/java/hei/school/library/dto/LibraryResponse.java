package hei.school.library.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LibraryResponse {

  private UUID id;
  private String name;
  private String phone;
  private String email;
  private String address;
}
