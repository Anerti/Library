package hei.school.library.mapper;

import hei.school.library.dto.GenreListResponse;
import hei.school.library.dto.GenreResponse;
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
    return GenreResponse.builder().id(genre.getId()).name(genre.getName()).build();
  }

  public GenreListResponse toPageResponse(Page<Genre> page, int pageNum, int pageSize) {
    return GenreListResponse.builder()
        .data(
            page.getContent().stream().map(this::toResponse).toList().isEmpty()
                ? null
                : page.getContent().stream().map(this::toResponse).toList())
        .meta(
            PaginationDto.builder()
                .page(pageNum)
                .size(pageSize)
                .total(page.getTotalElements())
                .build())
        .build();
  }
}
