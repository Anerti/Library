package hei.school.library.service.bookCopy;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import hei.school.library.dto.BookCopyResponse;
import hei.school.library.dto.PageResponse;
import hei.school.library.dto.PaginationDto;
import hei.school.library.entity.Book;
import hei.school.library.entity.BookCopy;
import hei.school.library.entity.Library;
import hei.school.library.entity.enums.BookCopyFormat;
import hei.school.library.entity.enums.BookCopyStatus;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.BookCopyMapper;
import hei.school.library.mapper.PaginationMapper;
import hei.school.library.repository.dao.BookCopyRepository;
import hei.school.library.repository.dao.LibraryRepository;
import hei.school.library.service.BookCopyService;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

@ExtendWith(MockitoExtension.class)
public class GetBookCopyServiceTest {

  @Mock private BookCopyRepository bookCopyRepository;

  @Mock private LibraryRepository libraryRepository;

  @Mock private BookCopyMapper bookCopyMapper;

  @Mock private PaginationMapper paginationMapper;

  @InjectMocks private BookCopyService bookCopyService;

  private UUID libraryId;
  private UUID copyId;
  private UUID bookId;
  private BookCopy bookCopy;
  private BookCopyResponse bookCopyResponse;

  @BeforeEach
  void setUp() {
    libraryId = UUID.randomUUID();
    copyId = UUID.randomUUID();
    bookId = UUID.randomUUID();

    Library library = Library.builder().id(libraryId).build();

    Book book = Book.builder().id(bookId).build();

    bookCopy =
        BookCopy.builder()
            .id(copyId)
            .library(library)
            .book(book)
            .price(25.0)
            .format(BookCopyFormat.PAPERBACK)
            .status(BookCopyStatus.AVAILABLE)
            .pageNumber(120)
            .updatedAt(LocalDateTime.now())
            .build();

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
  @DisplayName("findByFilters : return BookCopy list")
  void findByFilters_shouldReturnPageResponse() {
    Page<BookCopy> bookCopyPage = new PageImpl<>(List.of(bookCopy));
    PaginationDto pagination = PaginationDto.builder().page(1).size(20).total(1).build();

    when(libraryRepository.existsById(libraryId)).thenReturn(true);
    when(bookCopyRepository.findByFilters(
            eq(libraryId), isNull(), isNull(), isNull(), any(Pageable.class)))
        .thenReturn(bookCopyPage);
    when(bookCopyMapper.toResponse(bookCopy)).thenReturn(bookCopyResponse);
    when(paginationMapper.toPaginationDto(bookCopyPage, 1, 20)).thenReturn(pagination);

    PageResponse<BookCopyResponse> result =
        bookCopyService.findByFilters(libraryId, null, null, null, 1, 20);

    assertThat(result).isNotNull();
    assertThat(result.getData()).hasSize(1);
    assertThat(result.getData().get(0).getId()).isEqualTo(copyId);
    assertThat(result.getPagination().getTotal()).isEqualTo(1);
    verify(bookCopyRepository)
        .findByFilters(eq(libraryId), isNull(), isNull(), isNull(), any(Pageable.class));
  }

  @Test
  @DisplayName("findByFilters : return filtered list by status and format")
  void findByFilters_shouldReturnFilteredList() {
    Page<BookCopy> bookCopyPage = new PageImpl<>(List.of(bookCopy));
    PaginationDto pagination = PaginationDto.builder().page(1).size(20).total(1).build();

    when(libraryRepository.existsById(libraryId)).thenReturn(true);
    when(bookCopyRepository.findByFilters(
            eq(libraryId),
            eq(BookCopyStatus.AVAILABLE.name()),
            eq(BookCopyFormat.PAPERBACK.name()),
            isNull(),
            any(Pageable.class)))
        .thenReturn(bookCopyPage);
    when(bookCopyMapper.toResponse(bookCopy)).thenReturn(bookCopyResponse);
    when(paginationMapper.toPaginationDto(bookCopyPage, 1, 20)).thenReturn(pagination);

    PageResponse<BookCopyResponse> result =
        bookCopyService.findByFilters(
            libraryId, BookCopyStatus.AVAILABLE, BookCopyFormat.PAPERBACK, null, 1, 20);

    assertThat(result.getData()).hasSize(1);
    assertThat(result.getData().get(0).getStatus()).isEqualTo(BookCopyStatus.AVAILABLE);
    assertThat(result.getData().get(0).getFormat()).isEqualTo(BookCopyFormat.PAPERBACK);
  }

  @Test
  @DisplayName("findByFilters : throw not found exception if library is not found")
  void findByFilters_shouldThrow_whenLibraryNotFound() {
    when(libraryRepository.existsById(libraryId)).thenReturn(false);

    assertThatThrownBy(() -> bookCopyService.findByFilters(libraryId, null, null, null, 1, 20))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(libraryId.toString());

    verify(bookCopyRepository, never()).findByFilters(any(), any(), any(), any(), any());
  }
}
