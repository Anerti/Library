package hei.school.library.mapper;

import hei.school.library.dto.GenreResponse;
import hei.school.library.dto.PageResponse;
import hei.school.library.dto.PaginationDto;
import hei.school.library.entity.Genre;
import lombok.Builder;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Builder
@Component
public class GenreMapper {
  public GenreResponse toResponse(Genre genre) {
    if (genre == null) {
      return null;
    }
    return GenreResponse.builder()
        .id(genre.getId())
        .name(genre.getName())
        .createdAt(genre.getCreatedAt())
        .updatedAt(genre.getUpdatedAt())
        .build();
  }

  public PageResponse<GenreResponse> toPageResponse(Page<Genre> page, int pageNum, int pageSize) {
    return PageResponse.<GenreResponse>builder()
        .data(page.getContent().stream().map(this::toResponse).toList())
        .pagination(
            PaginationDto.builder()
                .page(pageNum)
                .size(pageSize)
                .total(page.getTotalElements())
                .build())
        .build();
  }
}
