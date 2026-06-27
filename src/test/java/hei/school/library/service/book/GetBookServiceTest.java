package hei.school.library.service.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hei.school.library.dto.BookResponse;
import hei.school.library.dto.PageResponse;
import hei.school.library.entity.Author;
import hei.school.library.entity.Book;
import hei.school.library.entity.Genre;
import hei.school.library.mapper.BookMapper;
import hei.school.library.mapper.PaginationMapper;
import hei.school.library.repository.dao.BookRepository;
import hei.school.library.service.BookService;
import hei.school.library.validator.DataValidator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class GetBookServiceTest {

  @Mock private BookRepository bookRepository;

  private BookService bookService;

  private Book bookWithAuthorsAndGenres;
  private Book bookWithoutRelations;
  @Mock private DataValidator dataValidator;

  @BeforeEach
  void setUp() {
    bookService =
        new BookService(bookRepository, new BookMapper(), dataValidator, new PaginationMapper());

    Author author =
        Author.builder()
            .id(UUID.randomUUID())
            .firstName("Antoine")
            .lastName("de Saint-Exupéry")
            .build();

    Genre genre = Genre.builder().id(UUID.randomUUID()).name("Fiction").build();

    bookWithAuthorsAndGenres =
        Book.builder()
            .id(UUID.randomUUID())
            .title("Le Petit Prince")
            .summary("Un classique")
            .isbn("978-2-07-061275-8")
            .publisher("Gallimard")
            .publishedAt(LocalDate.of(1943, 4, 6))
            .createdAt(LocalDateTime.now())
            .authors(Set.of(author))
            .genres(Set.of(genre))
            .build();

    bookWithoutRelations =
        Book.builder()
            .id(UUID.randomUUID())
            .title("Les Misérables")
            .isbn("978-2-07-040922-8")
            .publisher("Gallimard")
            .publishedAt(LocalDate.of(1862, 3, 30))
            .createdAt(LocalDateTime.now())
            .build();
  }

  @Test
  @DisplayName("listBooks: should return paginated books with authors and genres")
  void listBooks_shouldReturnPaginatedBooks() {
    Page<Book> page = new PageImpl<>(List.of(bookWithAuthorsAndGenres, bookWithoutRelations));

    when(bookRepository.searchBooks(eq(null), eq(null), eq(null), eq(null), any(PageRequest.class)))
        .thenReturn(page);

    PageResponse<BookResponse> result = bookService.listBooks(null, null, null, null, 1, 20);

    assertThat(result.getData()).hasSize(2);
    assertThat(result.getPagination().getPage()).isEqualTo(1);
    assertThat(result.getPagination().getSize()).isEqualTo(20);
    assertThat(result.getPagination().getTotal()).isEqualTo(2);

    BookResponse first = result.getData().get(0);
    assertThat(first.getTitle()).isEqualTo("Le Petit Prince");
    assertThat(first.getAuthors()).hasSize(1);
    assertThat(first.getAuthors().get(0).getLastName()).isEqualTo("de Saint-Exupéry");
    assertThat(first.getGenres()).hasSize(1);
    assertThat(first.getGenres().get(0).getName()).isEqualTo("Fiction");

    BookResponse second = result.getData().get(1);
    assertThat(second.getTitle()).isEqualTo("Les Misérables");
    assertThat(second.getAuthors()).isNull();
    assertThat(second.getGenres()).isNull();

    verify(bookRepository)
        .searchBooks(eq(null), eq(null), eq(null), eq(null), any(PageRequest.class));
  }

  @Test
  @DisplayName("listBooks: should filter by search term")
  void listBooks_shouldFilterBySearch() {
    Page<Book> page = new PageImpl<>(List.of(bookWithAuthorsAndGenres));

    when(bookRepository.searchBooks(
            eq("Petit"), eq(null), eq(null), eq(null), any(PageRequest.class)))
        .thenReturn(page);

    PageResponse<BookResponse> result = bookService.listBooks("Petit", null, null, null, 1, 20);

    assertThat(result.getData()).hasSize(1);
    assertThat(result.getData().get(0).getTitle()).isEqualTo("Le Petit Prince");
  }

  @Test
  @DisplayName("listBooks: should filter by author last name")
  void listBooks_shouldFilterByAuthorLastName() {
    Page<Book> page = new PageImpl<>(List.of(bookWithAuthorsAndGenres));

    when(bookRepository.searchBooks(
            eq(null), eq(null), eq("de Saint-Exupéry"), eq(null), any(PageRequest.class)))
        .thenReturn(page);

    PageResponse<BookResponse> result =
        bookService.listBooks(null, null, "de Saint-Exupéry", null, 1, 20);

    assertThat(result.getData()).hasSize(1);
  }

  @Test
  @DisplayName("listBooks: should filter by genre name")
  void listBooks_shouldFilterByGenreName() {
    Page<Book> page = new PageImpl<>(List.of(bookWithAuthorsAndGenres));

    when(bookRepository.searchBooks(
            eq(null), eq(null), eq(null), eq("Fiction"), any(PageRequest.class)))
        .thenReturn(page);

    PageResponse<BookResponse> result = bookService.listBooks(null, null, null, "Fiction", 1, 20);

    assertThat(result.getData()).hasSize(1);
  }

  @Test
  @DisplayName("listBooks: should filter by isbn")
  void listBooks_shouldFilterByIsbn() {
    Page<Book> page = new PageImpl<>(List.of(bookWithAuthorsAndGenres));

    when(bookRepository.searchBooks(
            eq(null), eq("978-2-07-061275-8"), eq(null), eq(null), any(PageRequest.class)))
        .thenReturn(page);

    PageResponse<BookResponse> result =
        bookService.listBooks(null, "978-2-07-061275-8", null, null, 1, 20);

    assertThat(result.getData()).hasSize(1);
    assertThat(result.getData().get(0).getIsbn()).isEqualTo("978-2-07-061275-8");
  }

  @Test
  @DisplayName("listBooks: should use page and size from parameters")
  void listBooks_shouldUsePageAndSize() {
    Page<Book> emptyPage = new PageImpl<>(List.of());

    when(bookRepository.searchBooks(eq(null), eq(null), eq(null), eq(null), any(PageRequest.class)))
        .thenReturn(emptyPage);

    PageResponse<BookResponse> result = bookService.listBooks(null, null, null, null, 3, 10);

    assertThat(result.getData()).isEmpty();
    assertThat(result.getPagination().getPage()).isEqualTo(3);
    assertThat(result.getPagination().getSize()).isEqualTo(10);
    assertThat(result.getPagination().getTotal()).isEqualTo(0);
  }
}
