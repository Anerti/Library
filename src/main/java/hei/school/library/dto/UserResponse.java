package hei.school.library.dto;

import hei.school.library.entity.enums.Role;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
  private UUID id;
  private String lastName;
  private String firstName;
  private LocalDate birthDate;
  private String email;
  private String phone;
  private Role role;
  private Instant createdAt;
  private Instant updatedAt;
}
