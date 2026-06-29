package hei.school.library.dto;

import java.time.LocalDate;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterRequest {
  private String lastName;
  private String firstName;
  private LocalDate birthDate;
  private String email;
  private String password;
  private String confirmPassword;
  private String phone;
}
