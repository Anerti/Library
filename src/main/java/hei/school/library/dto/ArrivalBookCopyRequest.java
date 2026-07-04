package hei.school.library.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ArrivalBookCopyRequest {
  private UUID bookCopyId;
  private BigDecimal purchasePrice;
  private Integer quantity;
}
