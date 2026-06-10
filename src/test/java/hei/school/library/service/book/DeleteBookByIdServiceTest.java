package hei.school.library.service.book;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.BookMapper;
import hei.school.library.mapper.PaginationMapper;
import hei.school.library.repository.dao.BookRepository;
import hei.school.library.service.BookService;
import hei.school.library.validator.BookValidator;
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
    bookService =
        new BookService(
            bookRepository, new BookMapper(), new DataValidator(), new PaginationMapper(), new BookValidator());

    existingId = UUID.randomUUID();
    unknownId = UUID.randomUUID();
  }

  @Test
  @DisplayName("deleteBook: should delete and not throw when book exists")
  void deleteBook_shouldNotThrow_whenExists() {
    when(bookRepository.deleteByIdAndReturn(existingId)).thenReturn(Optional.of(existingId));

    assertThatCode(() -> bookService.deleteBook(existingId)).doesNotThrowAnyException();

    verify(bookRepository).deleteByIdAndReturn(existingId);
  }

  @Test
  @DisplayName("deleteBook: should throw NotFoundException when book does not exist")
  void deleteBook_shouldThrow_whenNotFound() {
    when(bookRepository.deleteByIdAndReturn(unknownId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> bookService.deleteBook(unknownId))
        .isInstanceOf(NotFoundException.class)
        .hasMessage(String.format("Book %s not found", unknownId));
  }
}
