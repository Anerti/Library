package hei.school.library.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SaleBookCopyRequest {
  private UUID bookCopyId;
  private BigDecimal price;
}
