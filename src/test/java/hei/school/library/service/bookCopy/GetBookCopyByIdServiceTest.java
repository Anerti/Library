package hei.school.library.service.bookCopy;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import hei.school.library.dto.BookCopyResponse;
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

@ExtendWith(MockitoExtension.class)
public class GetBookCopyByIdServiceTest {

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
  @DisplayName("findById : return BookCopy if exist")
  void findById_shouldReturnBookCopy_whenExists() {
    when(libraryRepository.existsById(libraryId)).thenReturn(true);
    when(bookCopyRepository.findById(copyId)).thenReturn(Optional.of(bookCopy));
    when(bookCopyMapper.toResponse(bookCopy)).thenReturn(bookCopyResponse);

    BookCopyResponse result = bookCopyService.findById(libraryId, copyId);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(copyId);
    assertThat(result.getLibraryId()).isEqualTo(libraryId);
    verify(bookCopyRepository).findById(copyId);
  }

  @Test
  @DisplayName("findById : throw not found exception if library is not found")
  void findById_shouldThrow_whenLibraryNotFound() {
    when(libraryRepository.existsById(libraryId)).thenReturn(false);

    assertThatThrownBy(() -> bookCopyService.findById(libraryId, copyId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(libraryId.toString());

    verify(bookCopyRepository, never()).findById(any());
  }

  @Test
  @DisplayName("findById :  throw not found exception if bookCopy is missing")
  void findById_shouldThrow_whenBookCopyNotFound() {
    when(libraryRepository.existsById(libraryId)).thenReturn(true);
    when(bookCopyRepository.findById(copyId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> bookCopyService.findById(libraryId, copyId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(copyId.toString());

    verify(bookCopyRepository).findById(copyId);
  }
}
