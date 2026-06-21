package hei.school.library.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaleItemResponse {
  private UUID bookCopyId;
  private UUID saleId;
  private Instant createdAt;
  private Integer quantity;
  private BigDecimal price;
}
