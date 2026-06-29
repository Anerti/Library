package hei.school.library.controller.book;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import hei.school.library.dto.AuthorResponse;
import hei.school.library.dto.BookResponse;
import hei.school.library.dto.GenreSummary;
import hei.school.library.dto.PageResponse;
import hei.school.library.dto.PaginationDto;
import hei.school.library.endpoint.rest.controller.BookController;
import hei.school.library.exception.GlobalExceptionHandler;
import hei.school.library.config.JwtTokenProvider;
import hei.school.library.service.BookService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({BookController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
public class BookControllerTest {

  @Autowired private MockMvc mockMvc;
  @MockBean private BookService bookService;
  @MockBean private JwtTokenProvider jwtTokenProvider;

  @Test
  void should_list_books_with_pagination() throws Exception {
    AuthorResponse author =
        AuthorResponse.builder()
            .id(UUID.randomUUID())
            .firstName("Antoine")
            .lastName("de Saint-Exupéry")
            .build();

    GenreSummary genre = GenreSummary.builder().id(UUID.randomUUID()).name("Fiction").build();

    BookResponse book =
        BookResponse.builder()
            .id(UUID.randomUUID())
            .title("Le Petit Prince")
            .summary("Un classique")
            .isbn("978-2-07-061275-8")
            .publisher("Gallimard")
            .publishedAt(LocalDate.of(1943, 4, 6))
            .createdAt(LocalDateTime.now())
            .authors(List.of(author))
            .genres(List.of(genre))
            .build();

    PaginationDto pagination = PaginationDto.builder().page(1).size(20).total(1).build();

    PageResponse<BookResponse> response =
        PageResponse.<BookResponse>builder().data(List.of(book)).pagination(pagination).build();

    when(bookService.listBooks(eq(null), eq(null), eq(null), eq(null), eq(null), eq(1), eq(20)))
        .thenReturn(response);

    mockMvc
        .perform(get("/books").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data[0].title").value("Le Petit Prince"))
        .andExpect(jsonPath("$.data[0].isbn").value("978-2-07-061275-8"))
        .andExpect(jsonPath("$.data[0].publisher").value("Gallimard"))
        .andExpect(jsonPath("$.data[0].authors[0].lastName").value("de Saint-Exupéry"))
        .andExpect(jsonPath("$.data[0].genres[0].name").value("Fiction"))
        .andExpect(jsonPath("$.pagination.page").value(1))
        .andExpect(jsonPath("$.pagination.size").value(20))
        .andExpect(jsonPath("$.pagination.total").value(1));
  }

  @Test
  void should_list_books_with_search_query() throws Exception {
    BookResponse book =
        BookResponse.builder()
            .id(UUID.randomUUID())
            .title("Le Petit Prince")
            .isbn("978-2-07-061275-8")
            .publisher("Gallimard")
            .publishedAt(LocalDate.of(1943, 4, 6))
            .createdAt(LocalDateTime.now())
            .authors(List.of())
            .genres(List.of())
            .build();

    PaginationDto pagination = PaginationDto.builder().page(1).size(20).total(1).build();

    PageResponse<BookResponse> response =
        PageResponse.<BookResponse>builder().data(List.of(book)).pagination(pagination).build();

    when(bookService.listBooks(eq("Petit"), eq(null), eq(null), eq(null), eq(null), eq(1), eq(20)))
        .thenReturn(response);

    mockMvc
        .perform(get("/books").param("title", "Petit").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data[0].title").value("Le Petit Prince"));
  }

  @Test
  void should_filter_books_by_author_last_name() throws Exception {
    AuthorResponse author =
        AuthorResponse.builder()
            .id(UUID.randomUUID())
            .firstName("Antoine")
            .lastName("de Saint-Exupéry")
            .build();

    BookResponse book =
        BookResponse.builder()
            .id(UUID.randomUUID())
            .title("Le Petit Prince")
            .isbn("978-2-07-061275-8")
            .publisher("Gallimard")
            .publishedAt(LocalDate.of(1943, 4, 6))
            .createdAt(LocalDateTime.now())
            .authors(List.of(author))
            .genres(List.of())
            .build();

    PaginationDto pagination = PaginationDto.builder().page(1).size(20).total(1).build();

    PageResponse<BookResponse> response =
        PageResponse.<BookResponse>builder().data(List.of(book)).pagination(pagination).build();

    when(bookService.listBooks(
            eq(null), eq(null), eq(null), eq("de Saint-Exupéry"), eq(null), eq(1), eq(20)))
        .thenReturn(response);

    mockMvc
        .perform(
            get("/books").param("lastName", "de Saint-Exupéry").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data[0].title").value("Le Petit Prince"));
  }

  @Test
  void should_filter_books_by_genre_name() throws Exception {
    GenreSummary genre = GenreSummary.builder().id(UUID.randomUUID()).name("Fiction").build();

    BookResponse book =
        BookResponse.builder()
            .id(UUID.randomUUID())
            .title("Le Petit Prince")
            .isbn("978-2-07-061275-8")
            .publisher("Gallimard")
            .publishedAt(LocalDate.of(1943, 4, 6))
            .createdAt(LocalDateTime.now())
            .authors(List.of())
            .genres(List.of(genre))
            .build();

    PaginationDto pagination = PaginationDto.builder().page(1).size(20).total(1).build();

    PageResponse<BookResponse> response =
        PageResponse.<BookResponse>builder().data(List.of(book)).pagination(pagination).build();

    when(bookService.listBooks(
            eq(null), eq(null), eq(null), eq(null), eq("Fiction"), eq(1), eq(20)))
        .thenReturn(response);

    mockMvc
        .perform(get("/books").param("genre", "Fiction").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data[0].title").value("Le Petit Prince"));
  }

  @Test
  void should_filter_books_by_isbn() throws Exception {
    BookResponse book =
        BookResponse.builder()
            .id(UUID.randomUUID())
            .title("Le Petit Prince")
            .isbn("978-2-07-061275-8")
            .publisher("Gallimard")
            .publishedAt(LocalDate.of(1943, 4, 6))
            .createdAt(LocalDateTime.now())
            .authors(List.of())
            .genres(List.of())
            .build();

    PaginationDto pagination = PaginationDto.builder().page(1).size(20).total(1).build();

    PageResponse<BookResponse> response =
        PageResponse.<BookResponse>builder().data(List.of(book)).pagination(pagination).build();

    when(bookService.listBooks(
            eq(null), eq(null), eq("978-2-07-061275-8"), eq(null), eq(null), eq(1), eq(20)))
        .thenReturn(response);

    mockMvc
        .perform(
            get("/books").param("isbn", "978-2-07-061275-8").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data[0].isbn").value("978-2-07-061275-8"));
  }

  @Test
  void should_return_empty_list_when_no_books_match() throws Exception {
    PaginationDto pagination = PaginationDto.builder().page(1).size(20).total(0).build();

    PageResponse<BookResponse> response =
        PageResponse.<BookResponse>builder().data(null).pagination(pagination).build();

    when(bookService.listBooks(
            eq("nonexistent"), eq(null), eq(null), eq(null), eq(null), eq(1), eq(20)))
        .thenReturn(response);

    mockMvc
        .perform(get("/books").param("title", "nonexistent").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").value(nullValue()))
        .andExpect(jsonPath("$.pagination.total").value(0));
  }
}
