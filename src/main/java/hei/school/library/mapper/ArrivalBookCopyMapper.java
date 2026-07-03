package hei.school.library.mapper;

import hei.school.library.dto.ArrivalBookCopyResponse;
import hei.school.library.entity.ArrivalBookCopy;
import org.springframework.stereotype.Component;

@Component
public class ArrivalBookCopyMapper {
  public ArrivalBookCopyResponse toResponse(ArrivalBookCopy item) {
    return ArrivalBookCopyResponse.builder()
        .bookCopyId(item.getBookCopy().getId())
        .arrivalId(item.getArrival().getId())
        .createdAt(item.getCreatedAt())
        .purchasePrice(item.getPurchasePrice())
        .quantity(item.getQuantity())
        .updatedAt(item.getUpdatedAt())
        .build();
  }
}
