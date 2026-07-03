package hei.school.library.mapper;

import hei.school.library.dto.SaleBookCopyResponse;
import hei.school.library.entity.SaleBookCopy;
import org.springframework.stereotype.Component;

@Component
public class SaleBookCopyMapper {

  public SaleBookCopyResponse toResponse(SaleBookCopy saleBookCopy) {
    return SaleBookCopyResponse.builder()
        .bookCopyId(saleBookCopy.getBookCopy().getId())
        .saleId(saleBookCopy.getSale().getId())
        .createdAt(saleBookCopy.getCreatedAt())
        .quantity(saleBookCopy.getQuantity())
        .price(saleBookCopy.getPrice())
        .build();
  }
}
