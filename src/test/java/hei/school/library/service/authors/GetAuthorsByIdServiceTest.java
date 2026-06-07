package hei.school.library.service.authors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import hei.school.library.dto.AuthorResponse;
import hei.school.library.entity.Author;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.AuthorMapper;
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
class GetAuthorsByIdServiceTest {

  @Mock private AuthorRepository authorRepository;
  @Mock private AuthorValidator authorValidator;
  @Mock private DataValidator dataValidator;
  private AuthorService authorService;

  private UUID existingId;
  private UUID unknownId;
  private Author author;

  @BeforeEach
  void setUp() {
    AuthorMapper authorMapper = new AuthorMapper();
    authorService = new AuthorService(authorRepository, authorValidator, authorMapper, dataValidator);

    existingId = UUID.randomUUID();
    unknownId = UUID.randomUUID();
    author = Author.builder().id(existingId).firstName("Jean").lastName("Paul").build();
  }

  @Test
  @DisplayName("findById: should return author when found")
  void findById_shouldReturnAuthor() {
    when(authorRepository.findById(existingId)).thenReturn(Optional.of(author));
    AuthorResponse result = authorService.findById(existingId);

    assertThat(result.getLastName()).isEqualTo("Paul");
    verify(authorRepository).findById(existingId);
  }

  @Test
  @DisplayName("findById: should throw NotFoundException when absent")
  void findById_shouldThrow_whenNotFound() {
    when(authorRepository.findById(unknownId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authorService.findById(unknownId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(unknownId.toString());
  }
}
