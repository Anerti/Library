package hei.school.library.service.authors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import hei.school.library.exception.NotFoundException;
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
class DeleteAuthorsByIdServiceTest {

  @Mock private AuthorRepository authorRepository;
  @Mock private AuthorValidator authorValidator;
  @Mock private DataValidator dataValidator;
  private AuthorService authorService;

  private UUID existingId;
  private UUID unknownId;

  @BeforeEach
  void setUp() {
    AuthorMapper authorMapper = new AuthorMapper(new PaginationMapper());
    authorService =
        new AuthorService(authorRepository, authorValidator, authorMapper, dataValidator);

    existingId = UUID.randomUUID();
    unknownId = UUID.randomUUID();
  }

  @Test
  @DisplayName("delete: should delete when author exists")
  void delete_shouldDelete_whenExists() {
    when(authorRepository.delete(existingId)).thenReturn(Optional.of(existingId));

    authorService.delete(existingId);

    verify(authorRepository).delete(existingId);
  }

  @Test
  @DisplayName("delete: should throw NotFoundException when absent")
  void delete_shouldThrow_whenNotFound() {
    when(authorRepository.delete(unknownId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authorService.delete(unknownId)).isInstanceOf(NotFoundException.class);

    verify(authorRepository).delete(unknownId);
  }
}
