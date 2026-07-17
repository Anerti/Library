package hei.school.library.service.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hei.school.library.dto.BookRequest;
import hei.school.library.dto.BookResponse;
import hei.school.library.entity.Book;
import hei.school.library.exception.ConflictException;
import hei.school.library.exception.UnprocessableEntityException;
import hei.school.library.mapper.BookMapper;
import hei.school.library.mapper.PaginationMapper;
import hei.school.library.repository.dao.BookRepository;
import hei.school.library.service.BookService;
import hei.school.library.service.client.OpenLibraryClient;
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
class PostBookServiceTest {

  @Mock private BookRepository bookRepository;
  @Mock private OpenLibraryClient openLibraryClient;
  private BookService bookService;
  private BookRequest validRequest;

  @BeforeEach
  void setUp() {
    bookService =
        new BookService(
            bookRepository,
            new BookMapper(),
            new DataValidator(),
            new BookValidator(new DataValidator()),
            new PaginationMapper(),
            openLibraryClient);

    validRequest =
        BookRequest.builder()
            .title("Le Petit Prince")
            .summary("Un classique de la littérature française")
            .isbn("978-2-07-061275-8")
            .publisher("Gallimard")
            .publishedAt(LocalDate.of(1943, 4, 6))
            .build();
  }

  @Test
  @DisplayName(
      "createBook: should return response with empty authors and genres for a new book (per spec)")
  void createBook_shouldReturnEmptyAuthorsAndGenres() {
    UUID newId = UUID.randomUUID();
    Book savedBook =
        Book.builder()
            .id(newId)
            .title(validRequest.getTitle())
            .summary(validRequest.getSummary())
            .isbn(validRequest.getIsbn())
            .publisher(validRequest.getPublisher())
            .publishedAt(validRequest.getPublishedAt())
            .createdAt(LocalDateTime.now())
            .build();

    when(bookRepository.create(
            validRequest.getTitle(),
            validRequest.getSummary(),
            validRequest.getIsbn(),
            validRequest.getPublisher(),
            validRequest.getPublishedAt()))
        .thenReturn(Optional.of(savedBook));

    BookResponse result = bookService.createBook(validRequest);

    assertThat(result.getId()).isEqualTo(newId);
    assertThat(result.getTitle()).isEqualTo("Le Petit Prince");
    assertThat(result.getSummary()).isEqualTo("Un classique de la littérature française");
    assertThat(result.getIsbn()).isEqualTo("978-2-07-061275-8");
    assertThat(result.getPublisher()).isEqualTo("Gallimard");
    assertThat(result.getPublishedAt()).isEqualTo(LocalDate.of(1943, 4, 6));
    assertThat(result.getCreatedAt()).isNotNull();
    assertThat(result.getAuthors()).isNull();
    assertThat(result.getGenres()).isNull();

    verify(bookRepository)
        .create(
            validRequest.getTitle(),
            validRequest.getSummary(),
            validRequest.getIsbn(),
            validRequest.getPublisher(),
            validRequest.getPublishedAt());
  }

  @Test
  @DisplayName("createBook: should accept null summary")
  void createBook_shouldAcceptNullSummary() {
    BookRequest noSummary =
        BookRequest.builder()
            .title("Test")
            .isbn("0123456789")
            .publisher("Pub")
            .publishedAt(LocalDate.now())
            .build();

    Book saved =
        Book.builder()
            .id(UUID.randomUUID())
            .title("Test")
            .isbn("0123456789")
            .publisher("Pub")
            .publishedAt(LocalDate.now())
            .createdAt(LocalDateTime.now())
            .build();

    when(bookRepository.create("Test", null, "0123456789", "Pub", LocalDate.now()))
        .thenReturn(Optional.of(saved));

    BookResponse result = bookService.createBook(noSummary);

    assertThat(result.getSummary()).isNull();
  }

  @Test
  @DisplayName("createBook: should throw ConflictException when ISBN already exists")
  void createBook_shouldThrow_whenIsbnExists() {
    when(bookRepository.create(
            validRequest.getTitle(),
            validRequest.getSummary(),
            validRequest.getIsbn(),
            validRequest.getPublisher(),
            validRequest.getPublishedAt()))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> bookService.createBook(validRequest))
        .isInstanceOf(ConflictException.class)
        .hasMessage("Book's ISBN " + validRequest.getIsbn() + " already exists.");
  }

  @Test
  @DisplayName("createBook: should throw UnprocessableEntityException when title is missing")
  void createBook_shouldThrow_whenTitleMissing() {
    BookRequest invalid =
        BookRequest.builder()
            .isbn("0123456789")
            .publisher("Pub")
            .publishedAt(LocalDate.now())
            .build();

    assertThatThrownBy(() -> bookService.createBook(invalid))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessage("title is required and cannot be blank.");
  }

  @Test
  @DisplayName("createBook: should throw UnprocessableEntityException when isbn is missing")
  void createBook_shouldThrow_whenIsbnMissing() {
    BookRequest invalid =
        BookRequest.builder().title("Test").publisher("Pub").publishedAt(LocalDate.now()).build();

    assertThatThrownBy(() -> bookService.createBook(invalid))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessage("isbn is required and cannot be blank.");
  }

  @Test
  @DisplayName("createBook: should throw UnprocessableEntityException when publisher is missing")
  void createBook_shouldThrow_whenPublisherMissing() {
    BookRequest invalid =
        BookRequest.builder().title("Test").isbn("0123456789").publishedAt(LocalDate.now()).build();

    assertThatThrownBy(() -> bookService.createBook(invalid))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessage("publisher is required and cannot be blank.");
  }

  @Test
  @DisplayName("createBook: should throw UnprocessableEntityException when publishedAt is missing")
  void createBook_shouldThrow_whenPublishedAtMissing() {
    BookRequest invalid =
        BookRequest.builder().title("Test").isbn("0123456789").publisher("Pub").build();

    assertThatThrownBy(() -> bookService.createBook(invalid))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessage("publishedAt is required and cannot be blank.");
  }

  @Test
  @DisplayName(
      "createBook: should throw UnprocessableEntityException when isbn has invalid characters")
  void createBook_shouldThrow_whenIsbnHasInvalidChars() {
    BookRequest invalid =
        BookRequest.builder()
            .title("Test")
            .isbn("abcdefghij")
            .publisher("Pub")
            .publishedAt(LocalDate.now())
            .build();

    assertThatThrownBy(() -> bookService.createBook(invalid))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessage("isbn is invalid or contain Illegal characters.");
  }

  @Test
  @DisplayName("createBook: should throw UnprocessableEntityException when isbn is too short")
  void createBook_shouldThrow_whenIsbnTooShort() {
    BookRequest invalid =
        BookRequest.builder()
            .title("Test")
            .isbn("123456789")
            .publisher("Pub")
            .publishedAt(LocalDate.now())
            .build();

    assertThatThrownBy(() -> bookService.createBook(invalid))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessage("isbn is invalid or contain Illegal characters.");
  }

  @Test
  @DisplayName(
      "createBook: should throw UnprocessableEntityException when title has invalid characters")
  void createBook_shouldThrow_whenTitleHasInvalidChars() {
    BookRequest invalid =
        BookRequest.builder()
            .title("Harry Potter @ 3")
            .isbn("0123456789")
            .publisher("Bloomsbury")
            .publishedAt(LocalDate.now())
            .build();

    assertThatThrownBy(() -> bookService.createBook(invalid))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessage("title contains invalid characters.");
  }

  @Test
  @DisplayName(
      "createBook: should throw UnprocessableEntityException when publisher contains digits")
  void createBook_shouldThrow_whenPublisherContainsDigits() {
    BookRequest invalid =
        BookRequest.builder()
            .title("Test")
            .isbn("0123456789")
            .publisher("Pub 123")
            .publishedAt(LocalDate.now())
            .build();

    assertThatThrownBy(() -> bookService.createBook(invalid))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessage(
            "publisher field contain forbidden characters. Only letters (a-z, A-Z, éèê), hyphen and"
                + " space are allowed.");
  }
}
