package hei.school.library.service.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hei.school.library.dto.BookResponse;
import hei.school.library.entity.Book;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.BookMapper;
import hei.school.library.repository.dao.BookRepository;
import hei.school.library.service.BookService;
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
class GetBookServiceTest {

  @Mock private BookRepository bookRepository;

  private BookService bookService;

  private UUID existingId;
  private UUID unknownId;
  private Book book;

  @BeforeEach
  void setUp() {
    bookService = new BookService(bookRepository, new BookMapper());

    existingId = UUID.randomUUID();
    unknownId = UUID.randomUUID();

    book =
        Book.builder()
            .id(existingId)
            .title("Le Petit Prince")
            .summary("Un classique de la littérature française")
            .isbn("978-2-07-061275-8")
            .publisher("Gallimard")
            .publishedAt(LocalDate.of(1943, 4, 6))
            .createdAt(LocalDateTime.now())
            .build();
  }

  @Test
  @DisplayName("getBookById: should return mapped response when book exists")
  void getBookById_shouldReturnBook() {
    when(bookRepository.findById(existingId)).thenReturn(Optional.of(book));

    BookResponse result = bookService.getBookById(existingId);

    assertThat(result.getId()).isEqualTo(existingId);
    assertThat(result.getTitle()).isEqualTo("Le Petit Prince");
    assertThat(result.getSummary()).isEqualTo("Un classique de la littérature française");
    assertThat(result.getIsbn()).isEqualTo("978-2-07-061275-8");
    assertThat(result.getPublisher()).isEqualTo("Gallimard");
    assertThat(result.getPublishedAt()).isEqualTo(LocalDate.of(1943, 4, 6));
    assertThat(result.getCreatedAt()).isNotNull();
    assertThat(result.getAuthors()).isEmpty();
    assertThat(result.getGenres()).isEmpty();
    verify(bookRepository).findById(existingId);
  }

  @Test
  @DisplayName("getBookById: should throw NotFoundException when absent")
  void getBookById_shouldThrow_whenNotFound() {
    when(bookRepository.findById(unknownId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> bookService.getBookById(unknownId))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("Book not found");
  }
}
