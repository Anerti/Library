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
import hei.school.library.repository.dao.BookRepository;
import hei.school.library.service.BookService;
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
class CreateBookServiceTest {

  @Mock private BookRepository bookRepository;

  private BookService bookService;
  private BookRequest validRequest;

  @BeforeEach
  void setUp() {
    bookService = new BookService(bookRepository, new BookMapper(), new DataValidator());

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
    assertThat(result.getIsbn()).isEqualTo("978-2-07-061275-8");
    assertThat(result.getPublisher()).isEqualTo("Gallimard");
    assertThat(result.getPublishedAt()).isEqualTo(LocalDate.of(1943, 4, 6));
    assertThat(result.getCreatedAt()).isNotNull();

    // Spec: authors and genres are required arrays — empty for a new book
    assertThat(result.getAuthors()).isNotNull();
    assertThat(result.getAuthors()).isEmpty();
    assertThat(result.getGenres()).isNotNull();
    assertThat(result.getGenres()).isEmpty();

    verify(bookRepository)
        .create(
            validRequest.getTitle(),
            validRequest.getSummary(),
            validRequest.getIsbn(),
            validRequest.getPublisher(),
            validRequest.getPublishedAt());
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
        .hasMessage("Book with ISBN " + validRequest.getIsbn() + " already exists");
  }

  @Test
  @DisplayName("createBook: should throw UnprocessableEntityException when title is missing")
  void createBook_shouldThrow_whenTitleMissing() {
    BookRequest invalid = BookRequest.builder().isbn("123").publisher("Pub").publishedAt(LocalDate.now()).build();

    assertThatThrownBy(() -> bookService.createBook(invalid))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessage("title is required.");
  }

  @Test
  @DisplayName("createBook: should throw UnprocessableEntityException when isbn is missing")
  void createBook_shouldThrow_whenIsbnMissing() {
    BookRequest invalid =
        BookRequest.builder().title("Test").publisher("Pub").publishedAt(LocalDate.now()).build();

    assertThatThrownBy(() -> bookService.createBook(invalid))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessage("isbn is required.");
  }

  @Test
  @DisplayName("createBook: should throw UnprocessableEntityException when publisher is missing")
  void createBook_shouldThrow_whenPublisherMissing() {
    BookRequest invalid =
        BookRequest.builder().title("Test").isbn("123").publishedAt(LocalDate.now()).build();

    assertThatThrownBy(() -> bookService.createBook(invalid))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessage("publisher is required.");
  }

  @Test
  @DisplayName("createBook: should throw UnprocessableEntityException when publishedAt is missing")
  void createBook_shouldThrow_whenPublishedAtMissing() {
    BookRequest invalid = BookRequest.builder().title("Test").isbn("123").publisher("Pub").build();

    assertThatThrownBy(() -> bookService.createBook(invalid))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessage("publishedAt is required.");
  }
}
