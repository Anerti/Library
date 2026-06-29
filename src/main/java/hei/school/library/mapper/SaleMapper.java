package hei.school.library.mapper;

import hei.school.library.dto.LibraryResponse;
import hei.school.library.dto.PageResponse;
import hei.school.library.dto.PaginationDto;
import hei.school.library.dto.SaleResponse;
import hei.school.library.dto.UserResponse;
import hei.school.library.entity.Sale;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class SaleMapper {

  public SaleResponse toResponse(Sale sale, UserResponse user, LibraryResponse library) {
    return SaleResponse.builder()
        .id(sale.getId())
        .saleDate(sale.getSaleDate())
        .status(sale.getStatus())
        .user(user)
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
