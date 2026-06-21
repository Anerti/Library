package hei.school.library.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SaleItemRequest {
  private UUID bookCopyId;
  private Integer quantity;
  private BigDecimal price;
}
