package hei.school.library.service.authors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import hei.school.library.dto.AuthorRequest;
import hei.school.library.dto.AuthorResponse;
import hei.school.library.entity.Author;
import hei.school.library.exception.ConflictException;
import hei.school.library.exception.UnprocessableEntityException;
import hei.school.library.mapper.AuthorMapper;
import hei.school.library.mapper.PaginationMapper;
import hei.school.library.repository.dao.AuthorRepository;
import hei.school.library.service.AuthorService;
import hei.school.library.validator.AuthorValidator;
import hei.school.library.validator.DataValidator;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostAuthorsServiceTest {

  @Mock private AuthorRepository authorRepository;
  @Mock private AuthorValidator authorValidator;
  @Mock private DataValidator dataValidator;
  private AuthorService authorService;

  private UUID existingId;
  private Author author;
  private AuthorRequest authorRequest;

  @BeforeEach
  void setUp() {
    AuthorMapper authorMapper = new AuthorMapper(new PaginationMapper());
    authorService =
        new AuthorService(authorRepository, authorValidator, authorMapper, dataValidator);

    existingId = UUID.randomUUID();
    author = Author.builder().id(existingId).firstName("Jean").lastName("Paul").build();
    authorRequest = new AuthorRequest("Jean", "Paul");
  }

  @Test
  @DisplayName("create: should save and return DTO")
  void create_shouldSaveAndReturnDto() {
    when(authorRepository.create(anyString(), anyString())).thenReturn(Optional.of(author));

    AuthorResponse result = authorService.create(authorRequest);

    assertThat(result.getId()).isEqualTo(existingId);
    assertThat(result.getFirstName()).isEqualTo("Jean");
    assertThat(result.getLastName()).isEqualTo("Paul");
    verify(authorValidator).validateCreation(authorRequest);
  }

  @Test
  @DisplayName("create: should throw ConflictException when duplicate")
  void create_shouldThrow_whenDuplicate() {
    when(authorRepository.create(anyString(), anyString())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authorService.create(authorRequest))
        .isInstanceOf(ConflictException.class);
  }

  @Test
  @DisplayName("create: should throw UnprocessableEntityException when name contains numbers")
  void create_shouldThrow_whenNameInvalid() {
    AuthorRequest invalidRequest = new AuthorRequest("Jean123", "Paul");

    doThrow(new UnprocessableEntityException(
            "firstName field contain forbidden characters. Only letters (a-z, A-Z, éèê), hyphen and space are allowed."))
        .when(authorValidator)
        .validateCreation(invalidRequest);

    assertThatThrownBy(() -> authorService.create(invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class);
  }
}
