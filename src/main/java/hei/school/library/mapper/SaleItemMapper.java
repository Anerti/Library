package hei.school.library.mapper;

import hei.school.library.dto.SaleItemResponse;
import hei.school.library.entity.SaleItem;
import org.springframework.stereotype.Component;

@Component
public class SaleItemMapper {

  public SaleItemResponse toResponse(SaleItem saleItem) {
    return SaleItemResponse.builder()
        .bookCopyId(saleItem.getBookCopyId())
        .saleId(saleItem.getSaleId())
        .createdAt(saleItem.getCreatedAt())
        .quantity(saleItem.getQuantity())
        .price(saleItem.getPrice())
        .build();
  }
}
