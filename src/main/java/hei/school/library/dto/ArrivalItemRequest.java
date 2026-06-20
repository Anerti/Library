package hei.school.library.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ArrivalItemRequest {
  private UUID bookCopyId;
  private Double purchasePrice;
  private Integer quantity;
}
