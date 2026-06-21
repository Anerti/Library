package hei.school.library.mapper;

import hei.school.library.dto.CustomerResponse;
import hei.school.library.dto.LibraryResponse;
import hei.school.library.dto.PageResponse;
import hei.school.library.dto.PaginationDto;
import hei.school.library.dto.SaleResponse;
import hei.school.library.entity.Sale;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class SaleMapper {

  public SaleResponse toResponse(Sale sale, CustomerResponse customer, LibraryResponse library) {
    return SaleResponse.builder()
        .id(sale.getId())
        .saleDate(sale.getSaleDate())
        .status(sale.getStatus())
        .customer(customer)
        .library(library)
        .createdAt(sale.getCreatedAt())
        .build();
  }

  public PageResponse<SaleResponse> toPageResponse(
      Page<SaleResponse> page, int pageNum, int pageSize) {
    return PageResponse.<SaleResponse>builder()
        .data(page.getContent())
        .pagination(
            PaginationDto.builder()
                .page(pageNum)
                .size(pageSize)
                .total(page.getTotalElements())
                .build())
        .build();
  }
}
