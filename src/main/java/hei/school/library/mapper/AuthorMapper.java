package hei.school.library.mapper;

import hei.school.library.dto.AuthorResponse;
import hei.school.library.entity.Author;
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
}
