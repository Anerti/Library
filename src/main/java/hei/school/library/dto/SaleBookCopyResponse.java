package hei.school.library.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaleBookCopyResponse {
  private UUID bookCopyId;
  private UUID saleId;
  private Instant createdAt;
  private BigDecimal price;
}
