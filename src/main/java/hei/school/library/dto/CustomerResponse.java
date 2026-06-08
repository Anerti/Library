package hei.school.library.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerResponse {
  private UUID id;
  private String lastName;
  private String firstName;
  private LocalDate birthDate;
  private String email;
  private String phone;
  private Instant createdAt;
  private Instant updatedAt;
}
