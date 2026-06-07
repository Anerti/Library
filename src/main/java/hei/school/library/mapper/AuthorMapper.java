package hei.school.library.mapper;

import hei.school.library.dto.AuthorRequest;
import hei.school.library.dto.AuthorResponse;
import hei.school.library.dto.PageResponse;
import hei.school.library.dto.PaginationDto;
import hei.school.library.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapper {

  public AuthorResponse toResponse(Author author) {
    return AuthorResponse.builder()
        .id(author.getId())
        .firstName(author.getFirstName())
        .lastName(author.getLastName())
        .build();
  }

  public Author toEntity(AuthorRequest request) {
    return Author.builder()
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .build();
  }

  public PageResponse<AuthorResponse> toPageResponse(Page<Author> page, int pageNum, int pageSize) {
    return PageResponse.<AuthorResponse>builder()
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
