package hei.school.library.mapper;

import hei.school.library.dto.PaginationDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PaginationMapper {

  public PaginationDto toPaginationDto(Page<?> page, int pageNum, int pageSize) {
    return PaginationDto.builder()
        .page(pageNum)
        .size(pageSize)
        .total(page.getTotalElements())
        .build();
  }
}
