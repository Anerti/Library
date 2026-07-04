package hei.school.library.service.bookCopy;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import hei.school.library.dto.BookCopyRequest;
import hei.school.library.dto.BookCopyResponse;
import hei.school.library.entity.enums.BookCopyFormat;
import hei.school.library.entity.enums.BookCopyStatus;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.BookCopyMapper;
import hei.school.library.mapper.PaginationMapper;
import hei.school.library.repository.dao.BookCopyRepository;
import hei.school.library.repository.dao.BookRepository;
import hei.school.library.repository.dao.LibraryRepository;
import hei.school.library.service.BookCopyService;
import hei.school.library.validator.BookCopyValidator;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PostBookCopyServiceTest {

  @Mock private BookCopyRepository bookCopyRepository;
  @Mock private BookRepository bookRepository;
  @Mock private LibraryRepository libraryRepository;
  @Mock private BookCopyMapper bookCopyMapper;
  @Mock private BookCopyValidator bookCopyValidator;
  @Mock private PaginationMapper paginationMapper;

  @InjectMocks private BookCopyService bookCopyService;

  private UUID libraryId;
  private UUID copyId;
  private UUID bookId;
  private BookCopyRequest request;
  private BookCopyResponse bookCopyResponse;

  @BeforeEach
  void setUp() {
    libraryId = UUID.randomUUID();
    copyId = UUID.randomUUID();
    bookId = UUID.randomUUID();

    request = new BookCopyRequest(25.0, BookCopyFormat.PAPERBACK, bookId, 120);

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
  @DisplayName("create : crée et retourne le bookCopy")
  void create_shouldCreateAndReturnBookCopy() {
    when(libraryRepository.existsById(libraryId)).thenReturn(true);
    when(bookRepository.existsById(bookId)).thenReturn(true);
    when(bookCopyRepository.create(eq(25.0), eq("PAPERBACK"), eq(libraryId), eq(bookId), eq(120)))
        .thenReturn(Optional.of(new hei.school.library.entity.BookCopy()));
    when(bookCopyMapper.toResponse(any())).thenReturn(bookCopyResponse);

    BookCopyResponse result = bookCopyService.create(libraryId, request);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(copyId);
    assertThat(result.getLibraryId()).isEqualTo(libraryId);
    verify(bookCopyRepository)
        .create(eq(25.0), eq("PAPERBACK"), eq(libraryId), eq(bookId), eq(120));
  }

  @Test
  @DisplayName("create :  throw not found exception if library is not found")
  void create_shouldThrow_whenLibraryNotFound() {
    when(libraryRepository.existsById(libraryId)).thenReturn(false);

    assertThatThrownBy(() -> bookCopyService.create(libraryId, request))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(libraryId.toString());

    verify(bookCopyRepository, never()).create(any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("create : throw not found exception if bookCopy is missing")
  void create_shouldThrow_whenBookNotFound() {
    when(libraryRepository.existsById(libraryId)).thenReturn(true);
    when(bookRepository.existsById(bookId)).thenReturn(false);

    assertThatThrownBy(() -> bookCopyService.create(libraryId, request))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(bookId.toString());

    verify(bookCopyRepository, never()).create(any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("create : throw NotFoundException if insert failed")
  void create_shouldThrow_whenInsertFails() {
    when(libraryRepository.existsById(libraryId)).thenReturn(true);
    when(bookRepository.existsById(bookId)).thenReturn(true);
    when(bookCopyRepository.create(any(), any(), any(), any(), any())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> bookCopyService.create(libraryId, request))
        .isInstanceOf(NotFoundException.class);
  }
}
