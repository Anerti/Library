package hei.school.library.controller.bookCopy;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import hei.school.library.config.JwtTokenProvider;
import hei.school.library.dto.BookCopyRequest;
import hei.school.library.dto.BookCopyResponse;
import hei.school.library.dto.BookCopyUpdateRequest;
import hei.school.library.dto.PageResponse;
import hei.school.library.endpoint.rest.controller.BookCopyController;
import hei.school.library.entity.enums.BookCopyFormat;
import hei.school.library.entity.enums.BookCopyStatus;
import hei.school.library.exception.GlobalExceptionHandler;
import hei.school.library.exception.NotFoundException;
import hei.school.library.service.BookCopyService;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({BookCopyController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
public class BookCopyControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private BookCopyService bookCopyService;
  @MockBean private JwtTokenProvider jwtTokenProvider;

  private UUID libraryId;
  private UUID copyId;
  private UUID bookId;
  private BookCopyResponse bookCopyResponse;

  @BeforeEach
  void setUp() {
    libraryId = UUID.randomUUID();
    copyId = UUID.randomUUID();
    bookId = UUID.randomUUID();

    bookCopyResponse =
        BookCopyResponse.builder()
            .id(copyId)
            .libraryId(libraryId)
            .bookId(bookId)
            .price(25.0)
            .format(BookCopyFormat.PAPERBACK)
            .status(BookCopyStatus.AVAILABLE)
            .pageNumber(120)
            .updatedAt(LocalDateTime.now())
            .build();
  }

  @Test
  void should_get_book_copies_with_filters() throws Exception {
    PageResponse<BookCopyResponse> mockPageResponse = new PageResponse<>();

    when(bookCopyService.findByFilters(
            eq(libraryId),
            eq(BookCopyStatus.AVAILABLE),
            eq(BookCopyFormat.PAPERBACK),
            isNull(),
            eq(1),
            eq(20)))
        .thenReturn(mockPageResponse);

    mockMvc
        .perform(
            get("/libraries/{libraryId}/copies", libraryId)
                .param("status", "AVAILABLE")
                .param("format", "PAPERBACK"))
        .andExpect(status().isOk());

    verify(bookCopyService)
        .findByFilters(libraryId, BookCopyStatus.AVAILABLE, BookCopyFormat.PAPERBACK, null, 1, 20);
  }

  @Test
  void should_use_default_pagination_when_not_provided() throws Exception {
    PageResponse<BookCopyResponse> mockPageResponse = new PageResponse<>();

    when(bookCopyService.findByFilters(libraryId, null, null, null, 1, 20))
        .thenReturn(mockPageResponse);

    mockMvc.perform(get("/libraries/{libraryId}/copies", libraryId)).andExpect(status().isOk());

    verify(bookCopyService).findByFilters(libraryId, null, null, null, 1, 20);
  }

  @Test
  void should_return_not_found_when_library_does_not_exist_on_list() throws Exception {
    when(bookCopyService.findByFilters(libraryId, null, null, null, 1, 20))
        .thenThrow(new NotFoundException("Library with id " + libraryId + " not found"));

    mockMvc
        .perform(get("/libraries/{libraryId}/copies", libraryId))
        .andExpect(status().isNotFound());
  }

  @Test
  void should_get_book_copy_by_id() throws Exception {
    when(bookCopyService.findById(libraryId, copyId)).thenReturn(bookCopyResponse);

    mockMvc
        .perform(get("/libraries/{libraryId}/copies/{copyId}", libraryId, copyId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(copyId.toString()))
        .andExpect(jsonPath("$.format").value("PAPERBACK"));
  }

  @Test
  void should_return_not_found_when_book_copy_does_not_exist() throws Exception {
    when(bookCopyService.findById(libraryId, copyId))
        .thenThrow(new NotFoundException("BookCopy with id " + copyId + " not found"));

    mockMvc
        .perform(get("/libraries/{libraryId}/copies/{copyId}", libraryId, copyId))
        .andExpect(status().isNotFound());
  }

  @Test
  void should_create_book_copy() throws Exception {
    BookCopyRequest request = new BookCopyRequest(25.0, BookCopyFormat.PAPERBACK, bookId, 120);

    when(bookCopyService.create(eq(libraryId), any(BookCopyRequest.class)))
        .thenReturn(bookCopyResponse);

    mockMvc
        .perform(
            post("/libraries/{libraryId}/copies", libraryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(copyId.toString()))
        .andExpect(jsonPath("$.format").value("PAPERBACK"));
  }

  @Test
  void should_return_not_found_when_library_does_not_exist_on_create() throws Exception {
    BookCopyRequest request = new BookCopyRequest(25.0, BookCopyFormat.PAPERBACK, bookId, 120);

    when(bookCopyService.create(eq(libraryId), any(BookCopyRequest.class)))
        .thenThrow(new NotFoundException("Library with id " + libraryId + " not found"));

    mockMvc
        .perform(
            post("/libraries/{libraryId}/copies", libraryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  void should_return_not_found_when_book_does_not_exist_on_create() throws Exception {
    BookCopyRequest request = new BookCopyRequest(25.0, BookCopyFormat.PAPERBACK, bookId, 120);

    when(bookCopyService.create(eq(libraryId), any(BookCopyRequest.class)))
        .thenThrow(new NotFoundException("Book with id " + bookId + " not found"));

    mockMvc
        .perform(
            post("/libraries/{libraryId}/copies", libraryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  void should_update_book_copy() throws Exception {
    BookCopyUpdateRequest request = new BookCopyUpdateRequest(30.0, null, null, null);

    BookCopyResponse updatedResponse =
        BookCopyResponse.builder()
            .id(copyId)
            .libraryId(libraryId)
            .bookId(bookId)
            .price(30.0)
            .format(BookCopyFormat.PAPERBACK)
            .status(BookCopyStatus.AVAILABLE)
            .pageNumber(120)
            .updatedAt(LocalDateTime.now())
            .build();

    when(bookCopyService.update(eq(libraryId), eq(copyId), any(BookCopyUpdateRequest.class)))
        .thenReturn(updatedResponse);

    mockMvc
        .perform(
            patch("/libraries/{libraryId}/copies/{copyId}", libraryId, copyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.price").value(30.0));
  }

  @Test
  void should_return_not_found_when_book_copy_does_not_exist_on_update() throws Exception {
    BookCopyUpdateRequest request = new BookCopyUpdateRequest(30.0, null, null, null);

    when(bookCopyService.update(eq(libraryId), eq(copyId), any(BookCopyUpdateRequest.class)))
        .thenThrow(new NotFoundException("BookCopy with id " + copyId + " not found"));

    mockMvc
        .perform(
            patch("/libraries/{libraryId}/copies/{copyId}", libraryId, copyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  void should_delete_book_copy() throws Exception {
    doNothing().when(bookCopyService).delete(libraryId, copyId);

    mockMvc
        .perform(delete("/libraries/{libraryId}/copies/{copyId}", libraryId, copyId))
        .andExpect(status().isNoContent());

    verify(bookCopyService).delete(libraryId, copyId);
  }

  @Test
  void should_return_not_found_when_book_copy_does_not_exist_on_delete() throws Exception {
    doThrow(new NotFoundException("BookCopy with id " + copyId + " not found"))
        .when(bookCopyService)
        .delete(libraryId, copyId);

    mockMvc
        .perform(delete("/libraries/{libraryId}/copies/{copyId}", libraryId, copyId))
        .andExpect(status().isNotFound());
  }
}
