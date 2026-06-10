package hei.school.library.service.bookCopy;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
public class DeleteBookCopyServiceTest {

  @Mock private BookCopyRepository bookCopyRepository;
  @Mock private BookRepository bookRepository;
  @Mock private LibraryRepository libraryRepository;
  @Mock private BookCopyMapper bookCopyMapper;
  @Mock private PaginationMapper paginationMapper;

  @InjectMocks private BookCopyService bookCopyService;

  private UUID libraryId;
  private UUID copyId;

  @BeforeEach
  void setUp() {
    libraryId = UUID.randomUUID();
    copyId = UUID.randomUUID();
  }

  @Test
  @DisplayName("delete : supprime si library et bookCopy existent")
  void delete_shouldDelete_whenExists() {
    when(libraryRepository.existsById(libraryId)).thenReturn(true);
    when(bookCopyRepository.existsById(copyId)).thenReturn(true);

    bookCopyService.delete(libraryId, copyId);

    verify(bookCopyRepository).deleteById(copyId);
  }

  @Test
  @DisplayName("delete : lève NotFoundException si library absente")
  void delete_shouldThrow_whenLibraryNotFound() {
    when(libraryRepository.existsById(libraryId)).thenReturn(false);

    assertThatThrownBy(() -> bookCopyService.delete(libraryId, copyId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(libraryId.toString());

    verify(bookCopyRepository, never()).deleteById(any());
  }

  @Test
  @DisplayName("delete : lève NotFoundException si bookCopy absent")
  void delete_shouldThrow_whenBookCopyNotFound() {
    when(libraryRepository.existsById(libraryId)).thenReturn(true);
    when(bookCopyRepository.existsById(copyId)).thenReturn(false);

    assertThatThrownBy(() -> bookCopyService.delete(libraryId, copyId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(copyId.toString());

    verify(bookCopyRepository, never()).deleteById(any());
  }
}
