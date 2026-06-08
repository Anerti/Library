package hei.school.library.dto;

import hei.school.library.entity.Library;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArrivalResponse {
  private UUID id;
  private LocalDateTime createdAt;
  private LocalDateTime arrivalDate;
  private Library library;
}
