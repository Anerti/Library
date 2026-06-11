package hei.school.library.mapper;

import hei.school.library.dto.AuthorRequest;
import hei.school.library.dto.AuthorResponse;
import hei.school.library.dto.PageResponse;
import hei.school.library.entity.Author;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthorMapper {

  private final PaginationMapper paginationMapper;

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
        .pagination(paginationMapper.toPaginationDto(page, pageNum, pageSize))
        .build();
  }
}
