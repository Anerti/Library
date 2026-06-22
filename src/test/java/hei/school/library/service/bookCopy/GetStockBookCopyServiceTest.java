package hei.school.library.service.bookCopy;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import hei.school.library.entity.enums.BookCopyFormat;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.BookCopyMapper;
import hei.school.library.mapper.PaginationMapper;
import hei.school.library.repository.dao.BookCopyRepository;
import hei.school.library.repository.dao.BookRepository;
import hei.school.library.repository.dao.LibraryRepository;
import hei.school.library.service.BookCopyService;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GetStockBookCopyServiceTest {

  @Mock private BookCopyRepository bookCopyRepository;
  @Mock private BookRepository bookRepository;
  @Mock private LibraryRepository libraryRepository;
  @Mock private BookCopyMapper bookCopyMapper;
  @Mock private PaginationMapper paginationMapper;

  @InjectMocks private BookCopyService bookCopyService;

  private UUID bookId;
  private UUID libraryId;

  @BeforeEach
  void setUp() {
    bookId = UUID.randomUUID();
    libraryId = UUID.randomUUID();
  }

  @Test
  @DisplayName("getStock : should return total stock for all formats and all libraries")
  void getStock_shouldReturnTotalStock_whenFormatAndLibraryIdAreNull() {
    when(bookRepository.existsById(bookId)).thenReturn(true);
    when(bookCopyRepository.countAvailableStock(bookId, null, null)).thenReturn(20L);

    long result = bookCopyService.getStock(bookId, null, null);

    assertThat(result).isEqualTo(20L);
    verify(bookCopyRepository).countAvailableStock(bookId, null, null);
    verify(libraryRepository, never()).existsById(any());
  }

  @Test
  @DisplayName("getStock : should return stock for a specific format across all libraries")
  void getStock_shouldReturnStock_forSpecificFormat_allLibraries() {
    when(bookRepository.existsById(bookId)).thenReturn(true);
    when(bookCopyRepository.countAvailableStock(bookId, BookCopyFormat.PAPERBACK, null))
        .thenReturn(17L);

    long result = bookCopyService.getStock(bookId, BookCopyFormat.PAPERBACK, null);

    assertThat(result).isEqualTo(17L);
    verify(bookCopyRepository).countAvailableStock(bookId, BookCopyFormat.PAPERBACK, null);
  }

  @Test
  @DisplayName("getStock : should return total stock for all formats in a specific library")
  void getStock_shouldReturnStock_allFormats_specificLibrary() {
    when(bookRepository.existsById(bookId)).thenReturn(true);
    when(libraryRepository.existsById(libraryId)).thenReturn(true);
    when(bookCopyRepository.countAvailableStock(bookId, null, libraryId)).thenReturn(8L);

    long result = bookCopyService.getStock(bookId, null, libraryId);

    assertThat(result).isEqualTo(8L);
    verify(bookCopyRepository).countAvailableStock(bookId, null, libraryId);
  }

  @Test
  @DisplayName("getStock : should return stock for a specific format in a specific library")
  void getStock_shouldReturnStock_specificFormat_specificLibrary() {
    when(bookRepository.existsById(bookId)).thenReturn(true);
    when(libraryRepository.existsById(libraryId)).thenReturn(true);
    when(bookCopyRepository.countAvailableStock(bookId, BookCopyFormat.HARDCOVER, libraryId))
        .thenReturn(3L);

    long result = bookCopyService.getStock(bookId, BookCopyFormat.HARDCOVER, libraryId);

    assertThat(result).isEqualTo(3L);
    verify(bookCopyRepository).countAvailableStock(bookId, BookCopyFormat.HARDCOVER, libraryId);
  }

  @Test
  @DisplayName("getStock : should return zero when no copies available")
  void getStock_shouldReturnZero_whenNoCopiesAvailable() {
    when(bookRepository.existsById(bookId)).thenReturn(true);
    when(bookCopyRepository.countAvailableStock(bookId, BookCopyFormat.POCKET, null))
        .thenReturn(0L);

    long result = bookCopyService.getStock(bookId, BookCopyFormat.POCKET, null);

    assertThat(result).isEqualTo(0L);
  }

  @Test
  @DisplayName("getStock : should throw NotFoundException when book does not exist")
  void getStock_shouldThrow_whenBookNotFound() {
    when(bookRepository.existsById(bookId)).thenReturn(false);

    assertThatThrownBy(() -> bookCopyService.getStock(bookId, null, null))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(bookId.toString());

    verify(bookCopyRepository, never()).countAvailableStock(any(), any(), any());
  }

  @Test
  @DisplayName("getStock : should throw NotFoundException when library does not exist")
  void getStock_shouldThrow_whenLibraryNotFound() {
    when(bookRepository.existsById(bookId)).thenReturn(true);
    when(libraryRepository.existsById(libraryId)).thenReturn(false);

    assertThatThrownBy(() -> bookCopyService.getStock(bookId, null, libraryId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(libraryId.toString());

    verify(bookCopyRepository, never()).countAvailableStock(any(), any(), any());
  }
}
