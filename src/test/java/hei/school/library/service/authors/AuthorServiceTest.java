package hei.school.library.service.authors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import hei.school.library.dto.AuthorRequest;
import hei.school.library.dto.AuthorResponse;
import hei.school.library.dto.AuthorUpdateRequest;
import hei.school.library.entity.Author;
import hei.school.library.exception.ConflictException;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.AuthorMapper;
import hei.school.library.repository.dao.AuthorRepository;
import hei.school.library.service.AuthorService;
import hei.school.library.validator.AuthorValidator;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AuthorServiceTest {
  @Mock private AuthorRepository authorRepository;
  @Mock private AuthorValidator authorValidator;
  private AuthorMapper authorMapper;
  private AuthorService authorService;

  private UUID existingId;
  private UUID unknownId;
  private Author author;
  private AuthorRequest authorRequest;

  @BeforeEach
  public void setUp() {
    authorMapper = new AuthorMapper();
    authorService = new AuthorService(authorRepository, authorValidator, authorMapper);

    existingId = UUID.randomUUID();
    unknownId = UUID.randomUUID();
    author = Author.builder().id(existingId).firstName("Jean").lastName("Paul").build();

    authorRequest = new AuthorRequest("Jean", "Paul");
  }

  @Test
  @DisplayName("create: should save and return DTO")
  void create_shouldSaveAndReturnDto() {
    when(authorRepository.existsByFirstNameAndLastName("Jean", "Paul")).thenReturn(false);
    when(authorRepository.save(any(Author.class))).thenReturn(author);

    AuthorResponse result = authorService.create(authorRequest);

    assertThat(result.getId()).isEqualTo(existingId);
    assertThat(result.getFirstName()).isEqualTo("Jean");
    verify(authorValidator).validateCreate(authorRequest);
    verify(authorRepository).save(any(Author.class));
  }

  @Test
  @DisplayName("create: should throw ConflictException when duplicate")
  void create_shouldThrow_whenDuplicate() {
    when(authorRepository.existsByFirstNameAndLastName("Jean", "Paul")).thenReturn(true);

    assertThatThrownBy(() -> authorService.create(authorRequest))
        .isInstanceOf(ConflictException.class);

    verify(authorRepository, never()).save(any(Author.class));
  }

  @Test
  @DisplayName("update: should update and return DTO")
  void update_shouldUpdateAndReturnDto() {
    AuthorUpdateRequest updateReq = new AuthorUpdateRequest("Jean", "Paul Updated");

    Author updated =
        Author.builder().id(existingId).firstName("Jean").lastName("Paul Updated").build();

    when(authorRepository.findById(existingId)).thenReturn(Optional.of(author));
    when(authorRepository.save(any(Author.class))).thenReturn(updated);

    AuthorResponse result = authorService.update(existingId, updateReq);

    assertThat(result.getLastName()).isEqualTo("Paul Updated");
    verify(authorValidator).validateUpdate(updateReq);
  }

  @Test
  @DisplayName("update: should throw NotFoundException when absent")
  void update_shouldThrow_whenNotFound() {
    when(authorRepository.findById(unknownId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authorService.update(unknownId, new AuthorUpdateRequest()))
        .isInstanceOf(NotFoundException.class);

    verify(authorRepository, never()).save(any(Author.class));
  }

  @Test
  @DisplayName("delete: should delete when author exists")
  void delete_shouldDelete_whenExists() {
    when(authorRepository.existsById(existingId)).thenReturn(true);

    authorService.delete(existingId);

    verify(authorRepository).deleteById(existingId);
  }

  @Test
  @DisplayName("delete: should throw NotFoundException when absent")
  void delete_shouldThrow_whenNotFound() {
    when(authorRepository.existsById(unknownId)).thenReturn(false);

    assertThatThrownBy(() -> authorService.delete(unknownId)).isInstanceOf(NotFoundException.class);

    verify(authorRepository, never()).deleteById(any());
  }
}
