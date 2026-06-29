package hei.school.library.dto;

import hei.school.library.entity.enums.SaleStatus;
import java.time.Instant;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SaleRequest {
  private Instant saleDate;
  private UserRequest user;
  private SaleStatus status;
}
