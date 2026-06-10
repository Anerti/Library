package hei.school.library.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import hei.school.library.dto.BookRequest;
import hei.school.library.dto.BookResponse;
import hei.school.library.entity.Book;
import hei.school.library.repository.dao.BookRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

  @Mock private BookRepository bookRepository;

  @InjectMocks private BookService bookService;

  private Book book;
  private BookRequest bookRequest;
  private UUID bookId;

  @BeforeEach
  void setUp() {
    bookId = UUID.randomUUID();

    bookRequest =
        BookRequest.builder()
            .title("Le Petit Prince")
            .abstractText("Un classique de la littérature française")
            .isbn("978-2-07-061275-8")
            .publisher("Gallimard")
            .publishedAt(LocalDate.of(1943, 4, 6))
            .build();

    book =
        Book.builder()
            .id(bookId)
            .title("Le Petit Prince")
            .abstractText("Un classique de la littérature française")
            .isbn("978-2-07-061275-8")
            .publisher("Gallimard")
            .publishedAt(LocalDate.of(1943, 4, 6))
            .createdAt(LocalDateTime.now())
            .build();
  }

  // ==================== CREATE TESTS ====================

  @Test
  void should_create_book_successfully() {
    when(bookRepository.save(any(Book.class))).thenReturn(book);

    BookResponse result = bookService.createBook(bookRequest);

    assertNotNull(result);
    assertEquals(bookId, result.getId());
    assertEquals("Le Petit Prince", result.getTitle());
    assertEquals("978-2-07-061275-8", result.getIsbn());
    assertEquals("Gallimard", result.getPublisher());

    verify(bookRepository, times(1)).save(any(Book.class));
  }

  // ==================== READ TESTS ====================

  @Test
  void should_get_all_books_successfully() {
    List<Book> books = List.of(book);
    when(bookRepository.findAll()).thenReturn(books);

    List<BookResponse> result = bookService.getAllBooks();

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("Le Petit Prince", result.get(0).getTitle());

    verify(bookRepository, times(1)).findAll();
  }

  @Test
  void should_get_empty_list_when_no_books() {
    when(bookRepository.findAll()).thenReturn(List.of());

    List<BookResponse> result = bookService.getAllBooks();

    assertNotNull(result);
    assertTrue(result.isEmpty());

    verify(bookRepository, times(1)).findAll();
  }

  @Test
  void should_get_book_by_id_successfully() {
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

    BookResponse result = bookService.getBookById(bookId);

    assertNotNull(result);
    assertEquals(bookId, result.getId());
    assertEquals("Le Petit Prince", result.getTitle());

    verify(bookRepository, times(1)).findById(bookId);
  }

  @Test
  void should_throw_exception_when_book_not_found_by_id() {
    UUID nonExistentId = UUID.randomUUID();
    when(bookRepository.findById(nonExistentId)).thenReturn(Optional.empty());

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> bookService.getBookById(nonExistentId));

    assertEquals("Book not found", exception.getMessage());
    verify(bookRepository, times(1)).findById(nonExistentId);
  }

  // ==================== UPDATE TESTS ====================

  @Test
  void should_update_book_successfully() {
    BookRequest updateRequest =
        BookRequest.builder()
            .title("Le Petit Prince - Édition spéciale")
            .abstractText("Nouvelle édition avec illustrations")
            .isbn("978-2-07-061275-8")
            .publisher("Gallimard")
            .publishedAt(LocalDate.of(1943, 4, 6))
            .build();

    Book updatedBook =
        Book.builder()
            .id(bookId)
            .title("Le Petit Prince - Édition spéciale")
            .abstractText("Nouvelle édition avec illustrations")
            .isbn("978-2-07-061275-8")
            .publisher("Gallimard")
            .publishedAt(LocalDate.of(1943, 4, 6))
            .createdAt(LocalDateTime.now())
            .build();

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
    when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);

    BookResponse result = bookService.updateBook(bookId, updateRequest);

    assertNotNull(result);
    assertEquals(bookId, result.getId());
    assertEquals("Le Petit Prince - Édition spéciale", result.getTitle());
    assertEquals("Nouvelle édition avec illustrations", result.getAbstractText());

    verify(bookRepository, times(1)).findById(bookId);
    verify(bookRepository, times(1)).save(any(Book.class));
  }

  @Test
  void should_throw_exception_when_updating_non_existent_book() {
    UUID nonExistentId = UUID.randomUUID();
    when(bookRepository.findById(nonExistentId)).thenReturn(Optional.empty());

    RuntimeException exception =
        assertThrows(
            RuntimeException.class, () -> bookService.updateBook(nonExistentId, bookRequest));

    assertEquals("Book not found", exception.getMessage());
    verify(bookRepository, times(1)).findById(nonExistentId);
    verify(bookRepository, never()).save(any(Book.class));
  }

  // ==================== DELETE TESTS ====================

  @Test
  void should_delete_book_successfully() {
    when(bookRepository.existsById(bookId)).thenReturn(true);
    doNothing().when(bookRepository).deleteById(bookId);

    bookService.deleteBook(bookId);

    verify(bookRepository, times(1)).existsById(bookId);
    verify(bookRepository, times(1)).deleteById(bookId);
  }

  @Test
  void should_throw_exception_when_deleting_non_existent_book() {
    UUID nonExistentId = UUID.randomUUID();
    when(bookRepository.existsById(nonExistentId)).thenReturn(false);

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> bookService.deleteBook(nonExistentId));

    assertEquals("Book not found", exception.getMessage());
    verify(bookRepository, times(1)).existsById(nonExistentId);
    verify(bookRepository, never()).deleteById(any(UUID.class));
  }

  // ==================== SEARCH/PAGINATION TESTS ====================

  @Test
  void should_return_all_books_when_search_is_null() {
    Page<Book> page = new PageImpl<>(List.of(book));

    when(bookRepository.searchBooks(null, null, null, PageRequest.of(0, 20))).thenReturn(page);

    Map<String, Object> result = bookService.listBooks(null, null, null, 1, 20);

    assertNotNull(result);
    assertNotNull(result.get("data"));
    assertNotNull(result.get("pagination"));

    List<BookResponse> data = (List<BookResponse>) result.get("data");
    assertEquals(1, data.size());
    assertEquals("Le Petit Prince", data.get(0).getTitle());

    Map<String, Object> pagination = (Map<String, Object>) result.get("pagination");
    assertEquals(1, pagination.get("page"));
    assertEquals(20, pagination.get("size"));
    assertEquals(1L, pagination.get("total"));
  }

  @Test
  void should_return_all_books_when_search_is_empty() {
    Page<Book> page = new PageImpl<>(List.of(book));

    when(bookRepository.searchBooks("", null, null, PageRequest.of(0, 20))).thenReturn(page);

    Map<String, Object> result = bookService.listBooks("", null, null, 1, 20);

    assertNotNull(result);
    List<BookResponse> data = (List<BookResponse>) result.get("data");
    assertEquals(1, data.size());
  }

  @Test
  void should_return_filtered_books_when_search_is_valid() {
    Page<Book> page = new PageImpl<>(List.of(book));

    when(bookRepository.searchBooks("petit", null, null, PageRequest.of(0, 20))).thenReturn(page);

    Map<String, Object> result = bookService.listBooks("petit", null, null, 1, 20);

    assertNotNull(result);
    List<BookResponse> data = (List<BookResponse>) result.get("data");
    assertEquals(1, data.size());
    assertEquals("Le Petit Prince", data.get(0).getTitle());
  }

  @Test
  void should_use_correct_page_request_for_custom_pagination() {
    Page<Book> page = new PageImpl<>(List.of(book));

    when(bookRepository.searchBooks(any(), any(), any(), any(PageRequest.class))).thenReturn(page);

    bookService.listBooks(null, null, null, 3, 15);

    ArgumentCaptor<PageRequest> captor = ArgumentCaptor.forClass(PageRequest.class);
    verify(bookRepository).searchBooks(any(), any(), any(), captor.capture());

    assertEquals(2, captor.getValue().getPageNumber());
    assertEquals(15, captor.getValue().getPageSize());
  }

  @Test
  void should_return_empty_data_when_no_results() {
    Page<Book> emptyPage = new PageImpl<>(List.of());

    when(bookRepository.searchBooks("unknown", null, null, PageRequest.of(0, 20)))
        .thenReturn(emptyPage);

    Map<String, Object> result = bookService.listBooks("unknown", null, null, 1, 20);

    assertNotNull(result);
    List<BookResponse> data = (List<BookResponse>) result.get("data");
    assertTrue(data.isEmpty());

    Map<String, Object> pagination = (Map<String, Object>) result.get("pagination");
    assertEquals(0L, pagination.get("total"));
  }

  @Test
  void should_map_all_fields_correctly() {
    UUID id = UUID.randomUUID();
    Book customBook =
        Book.builder()
            .id(id)
            .title("Le Petit Prince")
            .abstractText("Test abstract")
            .isbn("978-2-07-061275-8")
            .publisher("Gallimard")
            .publishedAt(LocalDate.now().minusYears(5))
            .createdAt(LocalDateTime.now())
            .build();

    Page<Book> page = new PageImpl<>(List.of(customBook));

    when(bookRepository.searchBooks(any(), any(), any(), any(PageRequest.class))).thenReturn(page);

    Map<String, Object> result = bookService.listBooks(null, null, null, 1, 20);

    BookResponse dto = ((List<BookResponse>) result.get("data")).get(0);
    assertEquals(id, dto.getId());
    assertEquals("Le Petit Prince", dto.getTitle());
    assertEquals("978-2-07-061275-8", dto.getIsbn());
  }

  @Test
  void should_search_with_isbn_filter() {
    Page<Book> page = new PageImpl<>(List.of(book));

    when(bookRepository.searchBooks(null, "978-2-07-061275-8", null, PageRequest.of(0, 20)))
        .thenReturn(page);

    Map<String, Object> result = bookService.listBooks(null, "978-2-07-061275-8", null, 1, 20);

    assertNotNull(result);
    List<BookResponse> data = (List<BookResponse>) result.get("data");
    assertEquals(1, data.size());
    assertEquals("978-2-07-061275-8", data.get(0).getIsbn());
  }

  @Test
  void should_search_with_author_filter() {
    UUID authorId = UUID.randomUUID();
    Page<Book> page = new PageImpl<>(List.of(book));

    when(bookRepository.searchBooks(null, null, authorId, PageRequest.of(0, 20))).thenReturn(page);

    Map<String, Object> result = bookService.listBooks(null, null, authorId, 1, 20);

    assertNotNull(result);
    List<BookResponse> data = (List<BookResponse>) result.get("data");
    assertEquals(1, data.size());
  }
}
