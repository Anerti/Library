package hei.school.library.service.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hei.school.library.dto.BookResponse;
import hei.school.library.dto.BookUpdateRequest;
import hei.school.library.entity.Book;
import hei.school.library.exception.NotFoundException;
import hei.school.library.exception.UnprocessableEntityException;
import hei.school.library.mapper.BookMapper;
import hei.school.library.mapper.PaginationMapper;
import hei.school.library.repository.dao.BookRepository;
import hei.school.library.service.BookService;
import hei.school.library.validator.BookValidator;
import hei.school.library.validator.DataValidator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PatchBookByIdTest {

  @Mock private BookRepository bookRepository;

  private BookService bookService;

  private UUID bookId;
  private Book existingBook;

  @BeforeEach
  void setUp() {
    bookService =
        new BookService(
            bookRepository,
            new BookMapper(),
            new DataValidator(),
            new BookValidator(new DataValidator()),
            new PaginationMapper());

    bookId = UUID.randomUUID();
    existingBook =
        Book.builder()
            .id(bookId)
            .title("Le Petit Prince")
            .summary("Un classique de la littérature française")
            .isbn("978-2-07-061275-8")
            .publisher("Gallimard")
            .publishedAt(LocalDate.of(1943, 4, 6))
            .createdAt(LocalDateTime.now())
            .build();
  }

  @Test
  @DisplayName("updateBook: should update all fields and return mapped response")
  void updateBook_shouldUpdateAllFields() {
    BookUpdateRequest request = new BookUpdateRequest();
    request.setTitle("L'Etranger");
    request.setSummary("Un roman existentialiste d Albert Camus");
    request.setIsbn("978-2-07-036002-4");
    request.setPublisher("Gallimard");
    request.setPublishedAt(LocalDate.of(1942, 6, 15));

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
    when(bookRepository.save(any(Book.class))).thenReturn(existingBook);

    BookResponse result = bookService.updateBook(bookId, request);

    assertThat(result.getId()).isEqualTo(bookId);
    assertThat(result.getTitle()).isEqualTo("L'Etranger");
    assertThat(result.getSummary()).isEqualTo("Un roman existentialiste d Albert Camus");
    assertThat(result.getIsbn()).isEqualTo("978-2-07-036002-4");
    assertThat(result.getPublisher()).isEqualTo("Gallimard");
    assertThat(result.getPublishedAt()).isEqualTo(LocalDate.of(1942, 6, 15));

    verify(bookRepository).findById(bookId);
    verify(bookRepository).save(existingBook);
  }

  @Test
  @DisplayName("updateBook: should partially update title only")
  void updateBook_shouldUpdateTitleOnly() {
    BookUpdateRequest request = new BookUpdateRequest();
    request.setTitle("Le Petit Prince Revised");

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
    when(bookRepository.save(any(Book.class))).thenReturn(existingBook);

    BookResponse result = bookService.updateBook(bookId, request);

    assertThat(result.getTitle()).isEqualTo("Le Petit Prince Revised");
    assertThat(result.getSummary()).isEqualTo("Un classique de la littérature française");
    assertThat(result.getIsbn()).isEqualTo("978-2-07-061275-8");
    assertThat(result.getPublisher()).isEqualTo("Gallimard");
    assertThat(result.getPublishedAt()).isEqualTo(LocalDate.of(1943, 4, 6));
  }

  @Test
  @DisplayName("updateBook: should partially update summary only")
  void updateBook_shouldUpdateSummaryOnly() {
    BookUpdateRequest request = new BookUpdateRequest();
    request.setSummary("Nouveau resume mis a jour");

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
    when(bookRepository.save(any(Book.class))).thenReturn(existingBook);

    BookResponse result = bookService.updateBook(bookId, request);

    assertThat(result.getTitle()).isEqualTo("Le Petit Prince");
    assertThat(result.getSummary()).isEqualTo("Nouveau resume mis a jour");
    assertThat(result.getIsbn()).isEqualTo("978-2-07-061275-8");
    assertThat(result.getPublisher()).isEqualTo("Gallimard");
    assertThat(result.getPublishedAt()).isEqualTo(LocalDate.of(1943, 4, 6));
  }

  @Test
  @DisplayName("updateBook: should partially update ISBN only")
  void updateBook_shouldUpdateIsbnOnly() {
    BookUpdateRequest request = new BookUpdateRequest();
    request.setIsbn("978-0-14-044926-6");

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
    when(bookRepository.save(any(Book.class))).thenReturn(existingBook);

    BookResponse result = bookService.updateBook(bookId, request);

    assertThat(result.getTitle()).isEqualTo("Le Petit Prince");
    assertThat(result.getSummary()).isEqualTo("Un classique de la littérature française");
    assertThat(result.getIsbn()).isEqualTo("978-0-14-044926-6");
    assertThat(result.getPublisher()).isEqualTo("Gallimard");
    assertThat(result.getPublishedAt()).isEqualTo(LocalDate.of(1943, 4, 6));
  }

  @Test
  @DisplayName("updateBook: should partially update publisher only")
  void updateBook_shouldUpdatePublisherOnly() {
    BookUpdateRequest request = new BookUpdateRequest();
    request.setPublisher("Hachette");

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
    when(bookRepository.save(any(Book.class))).thenReturn(existingBook);

    BookResponse result = bookService.updateBook(bookId, request);

    assertThat(result.getTitle()).isEqualTo("Le Petit Prince");
    assertThat(result.getSummary()).isEqualTo("Un classique de la littérature française");
    assertThat(result.getIsbn()).isEqualTo("978-2-07-061275-8");
    assertThat(result.getPublisher()).isEqualTo("Hachette");
    assertThat(result.getPublishedAt()).isEqualTo(LocalDate.of(1943, 4, 6));
  }

  @Test
  @DisplayName("updateBook: should partially update publishedAt only")
  void updateBook_shouldUpdatePublishedAtOnly() {
    BookUpdateRequest request = new BookUpdateRequest();
    request.setPublishedAt(LocalDate.of(1950, 1, 1));

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
    when(bookRepository.save(any(Book.class))).thenReturn(existingBook);

    BookResponse result = bookService.updateBook(bookId, request);

    assertThat(result.getTitle()).isEqualTo("Le Petit Prince");
    assertThat(result.getSummary()).isEqualTo("Un classique de la littérature française");
    assertThat(result.getIsbn()).isEqualTo("978-2-07-061275-8");
    assertThat(result.getPublisher()).isEqualTo("Gallimard");
    assertThat(result.getPublishedAt()).isEqualTo(LocalDate.of(1950, 1, 1));
  }

  @Test
  @DisplayName("updateBook: should throw NotFoundException when book does not exist")
  void updateBook_shouldThrow_whenBookNotFound() {
    UUID unknownId = UUID.randomUUID();
    BookUpdateRequest request = new BookUpdateRequest();
    request.setTitle("Any Title");

    when(bookRepository.findById(unknownId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> bookService.updateBook(unknownId, request))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("Book " + unknownId + " not found");
  }

  @Test
  @DisplayName("updateBook: should throw UnprocessableEntityException when no fields provided")
  void updateBook_shouldThrow_whenNoFieldsProvided() {
    BookUpdateRequest request = new BookUpdateRequest();

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));

    assertThatThrownBy(() -> bookService.updateBook(bookId, request))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessage(
            "At least one field (title, summary, isbn, publisher, publishedAt) must be provided");
  }

  @Test
  @DisplayName("updateBook: should throw when title has invalid characters")
  void updateBook_shouldThrow_whenTitleHasInvalidChars() {
    BookUpdateRequest request = new BookUpdateRequest();
    request.setTitle("Invalid Title @ 123");

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));

    assertThatThrownBy(() -> bookService.updateBook(bookId, request))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessage(
            "title field contain forbidden characters. Only letters (a-z, A-Z, éèê), hyphen and"
                + " space are allowed.");
  }

  @Test
  @DisplayName("updateBook: should throw when ISBN has invalid characters")
  void updateBook_shouldThrow_whenIsbnHasInvalidChars() {
    BookUpdateRequest request = new BookUpdateRequest();
    request.setIsbn("abcdefghij");

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));

    assertThatThrownBy(() -> bookService.updateBook(bookId, request))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessage("isbn is invalid or contain Illegal characters.");
  }

  @Test
  @DisplayName("updateBook: should throw when ISBN is too short")
  void updateBook_shouldThrow_whenIsbnTooShort() {
    BookUpdateRequest request = new BookUpdateRequest();
    request.setIsbn("123456789");

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));

    assertThatThrownBy(() -> bookService.updateBook(bookId, request))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessage("isbn is invalid or contain Illegal characters.");
  }

  @Test
  @DisplayName("updateBook: should throw when publisher has invalid characters")
  void updateBook_shouldThrow_whenPublisherHasInvalidChars() {
    BookUpdateRequest request = new BookUpdateRequest();
    request.setPublisher("Publisher 123");

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));

    assertThatThrownBy(() -> bookService.updateBook(bookId, request))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessage(
            "publisher field contain forbidden characters. Only letters (a-z, A-Z, éèê), hyphen and"
                + " space are allowed.");
  }
}
