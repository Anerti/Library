package hei.school.library.service.authors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hei.school.library.dto.AuthorResponse;
import hei.school.library.dto.PageResponse;
import hei.school.library.entity.Author;
import hei.school.library.mapper.AuthorMapper;
import hei.school.library.repository.dao.AuthorRepository;
import hei.school.library.service.AuthorService;
import hei.school.library.validator.AuthorValidator;
import hei.school.library.validator.DataValidator;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class GetAuthorsServiceTest {

  @Mock private AuthorRepository authorRepository;
  @Mock private AuthorValidator authorValidator;
  @Mock private DataValidator dataValidator;
  private AuthorService authorService;
  private Author author;

  @BeforeEach
  void setUp() {
    AuthorMapper authorMapper = new AuthorMapper();
    authorService =
        new AuthorService(authorRepository, authorValidator, authorMapper, dataValidator);

    UUID existingId = UUID.randomUUID();
    author = Author.builder().id(existingId).firstName("Jean").lastName("Paul").build();
  }

  @Test
  @DisplayName("findAll: should return page of authors")
  void findAll_shouldReturnPageOfAuthors() {
    Page<Author> authorPage = new PageImpl<>(List.of(author));
    when(authorRepository.findAll(any(Pageable.class))).thenReturn(authorPage);

    PageResponse<AuthorResponse> result = authorService.findAll(null, 1, 20);

    assertThat(result.getData()).hasSize(1);
    assertThat(result.getData().getFirst().getFirstName()).isEqualTo("Jean");
    assertThat(result.getPagination().getTotal()).isEqualTo(1);
    assertThat(result.getPagination().getPage()).isEqualTo(1);
    assertThat(result.getPagination().getSize()).isEqualTo(20);
    verify(authorRepository).findAll(any(Pageable.class));
  }

  @Test
  @DisplayName("findAll: should search by keyword")
  void findAll_shouldSearchAuthors() {
    Page<Author> authorPage = new PageImpl<>(List.of(author));
    when(authorRepository.findBySearch(any(), any(Pageable.class))).thenReturn(authorPage);

    PageResponse<AuthorResponse> result = authorService.findAll("Jean", 1, 20);

    assertThat(result.getData()).hasSize(1);
    assertThat(result.getData().getFirst().getFirstName()).isEqualTo("Jean");
    verify(authorRepository).findBySearch("Jean", PageRequest.of(0, 20));
  }
}
