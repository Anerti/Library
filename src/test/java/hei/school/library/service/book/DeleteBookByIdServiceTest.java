package hei.school.library.service.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.BookMapper;
import hei.school.library.repository.dao.BookRepository;
import hei.school.library.service.BookService;
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
class DeleteBookByIdServiceTest {

  @Mock private BookRepository bookRepository;

  private BookService bookService;

  private UUID existingId;
  private UUID unknownId;

  @BeforeEach
  void setUp() {
    bookService = new BookService(bookRepository, new BookMapper(), new DataValidator());

    existingId = UUID.randomUUID();
    unknownId = UUID.randomUUID();
  }

  @Test
  @DisplayName("deleteBook: should return the deleted book ID when book exists")
  void deleteBook_shouldReturnId() {
    when(bookRepository.deleteByIdAndReturn(existingId)).thenReturn(Optional.of(existingId));

    UUID result = bookService.deleteBook(existingId);

    assertThat(result).isEqualTo(existingId);
    verify(bookRepository).deleteByIdAndReturn(existingId);
  }

  @Test
  @DisplayName("deleteBook: should throw NotFoundException when book does not exist")
  void deleteBook_shouldThrow_whenNotFound() {
    when(bookRepository.deleteByIdAndReturn(unknownId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> bookService.deleteBook(unknownId))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("Book not found with id: " + unknownId);
  }
}
