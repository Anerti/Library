package hei.school.library.service.bookCopy;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import hei.school.library.dto.BookCopyResponse;
import hei.school.library.dto.BookCopyUpdateRequest;
import hei.school.library.entity.BookCopy;
import hei.school.library.entity.enums.BookCopyFormat;
import hei.school.library.entity.enums.BookCopyStatus;
import hei.school.library.exception.BadRequestException;
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
public class PatchBookCopyServiceTest {

  @Mock private BookCopyRepository bookCopyRepository;
  @Mock private BookRepository bookRepository;
  @Mock private LibraryRepository libraryRepository;
  @Mock private BookCopyValidator bookCopyValidator;
  @Mock private BookCopyMapper bookCopyMapper;
  @Mock private PaginationMapper paginationMapper;

  @InjectMocks private BookCopyService bookCopyService;

  private UUID libraryId;
  private UUID copyId;
  private BookCopy bookCopy;
  private BookCopyResponse bookCopyResponse;

  @BeforeEach
  void setUp() {
    libraryId = UUID.randomUUID();
    copyId = UUID.randomUUID();

    bookCopy =
        BookCopy.builder()
            .id(copyId)
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
            .price(30.0)
            .format(BookCopyFormat.HARDCOVER)
            .status(BookCopyStatus.AVAILABLE)
            .pageNumber(150)
            .updatedAt(LocalDateTime.now())
            .build();
  }

  @Test
  @DisplayName("update : updates and returns the bookCopy")
  void update_shouldUpdateAndReturnBookCopy() {
    BookCopyUpdateRequest request =
        new BookCopyUpdateRequest(30.0, BookCopyFormat.HARDCOVER, null, 150);

    when(bookCopyRepository.findById(copyId)).thenReturn(Optional.of(bookCopy));
    when(bookCopyRepository.save(any(BookCopy.class))).thenReturn(bookCopy);
    when(bookCopyMapper.toResponse(any(BookCopy.class))).thenReturn(bookCopyResponse);

    BookCopyResponse result = bookCopyService.update(libraryId, copyId, request);

    assertThat(result).isNotNull();
    assertThat(result.getPrice()).isEqualTo(30.0);
    assertThat(result.getFormat()).isEqualTo(BookCopyFormat.HARDCOVER);
    verify(bookCopyRepository).save(any(BookCopy.class));
  }

  @Test
  @DisplayName("update : throw BadRequestException if all fields are null")
  void update_shouldThrow_whenAllFieldsNull() {
    BookCopyUpdateRequest request = new BookCopyUpdateRequest(null, null, null, null);

    doThrow(new BadRequestException("At least one field is required"))
        .when(bookCopyValidator)
        .validateUpdate(request);

    assertThatThrownBy(() -> bookCopyService.update(libraryId, copyId, request))
        .isInstanceOf(BadRequestException.class);

    verify(bookCopyRepository, never()).findById(any());
    verify(bookCopyRepository, never()).save(any());
  }

  @Test
  @DisplayName("update : throw NotFoundException si bookCopy missing")
  void update_shouldThrow_whenBookCopyNotFound() {
    BookCopyUpdateRequest request = new BookCopyUpdateRequest(30.0, null, null, null);

    when(bookCopyRepository.findById(copyId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> bookCopyService.update(libraryId, copyId, request))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(copyId.toString());

    verify(bookCopyRepository, never()).save(any());
  }
}
