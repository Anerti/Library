package hei.school.library.service.analytics;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import hei.school.library.entity.enums.BookCopyFormat;
import hei.school.library.exception.NotFoundException;
import hei.school.library.repository.dao.AnalyticsRepository;
import hei.school.library.repository.dao.BookRepository;
import hei.school.library.repository.dao.LibraryRepository;
import hei.school.library.service.AnalyticsService;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AnalyticsServiceTest {

  @Mock private LibraryRepository libraryRepository;
  @Mock private BookRepository bookRepository;
  @Mock private AnalyticsRepository analyticsRepository;

  @InjectMocks private AnalyticsService analyticsService;

  private UUID libraryId;
  private UUID bookId;

  @BeforeEach
  void setUp() {
    libraryId = UUID.randomUUID();
    bookId = UUID.randomUUID();
  }

  @Test
  @DisplayName("getStockOverview : should return total stock for all formats")
  void getStockOverview_shouldReturnTotal_whenFormatIsAll() {
    when(libraryRepository.existsById(libraryId)).thenReturn(true);
    when(bookRepository.existsById(bookId)).thenReturn(true);
    when(analyticsRepository.countAvailableStock(bookId, null, libraryId)).thenReturn(20L);

    var result = analyticsService.getStockOverview(libraryId, bookId, "ALL");

    assertThat(result.getBookId()).isEqualTo(bookId);
    assertThat(result.getTotal()).isEqualTo(20);
    assertThat(result.getByFormat()).isNull();
  }

  @Test
  @DisplayName("getStockOverview : should return stock for a specific format")
  void getStockOverview_shouldReturnStock_forSpecificFormat() {
    when(libraryRepository.existsById(libraryId)).thenReturn(true);
    when(bookRepository.existsById(bookId)).thenReturn(true);
    when(analyticsRepository.countAvailableStock(bookId, BookCopyFormat.PAPERBACK, libraryId))
        .thenReturn(7L);

    var result = analyticsService.getStockOverview(libraryId, bookId, "PAPERBACK");

    assertThat(result.getBookId()).isEqualTo(bookId);
    assertThat(result.getTotal()).isEqualTo(7);
    assertThat(result.getByFormat()).isEqualTo(BookCopyFormat.PAPERBACK);
  }

  @Test
  @DisplayName("getStockOverview : should default to ALL when format is null")
  void getStockOverview_shouldDefaultToAll_whenFormatIsNull() {
    when(libraryRepository.existsById(libraryId)).thenReturn(true);
    when(bookRepository.existsById(bookId)).thenReturn(true);
    when(analyticsRepository.countAvailableStock(bookId, null, libraryId)).thenReturn(15L);

    var result = analyticsService.getStockOverview(libraryId, bookId, null);

    assertThat(result.getBookId()).isEqualTo(bookId);
    assertThat(result.getTotal()).isEqualTo(15);
    assertThat(result.getByFormat()).isNull();
  }

  @Test
  @DisplayName("getStockOverview : should return zero when no copies available")
  void getStockOverview_shouldReturnZero_whenNoCopies() {
    when(libraryRepository.existsById(libraryId)).thenReturn(true);
    when(bookRepository.existsById(bookId)).thenReturn(true);
    when(analyticsRepository.countAvailableStock(bookId, BookCopyFormat.POCKET, libraryId))
        .thenReturn(0L);

    var result = analyticsService.getStockOverview(libraryId, bookId, "POCKET");

    assertThat(result.getTotal()).isZero();
  }

  @Test
  @DisplayName("getStockOverview : should throw NotFoundException when library not found")
  void getStockOverview_shouldThrow_whenLibraryNotFound() {
    when(libraryRepository.existsById(libraryId)).thenReturn(false);

    assertThatThrownBy(
            () -> analyticsService.getStockOverview(libraryId, bookId, "ALL"))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(libraryId.toString());

    verify(analyticsRepository, never()).countAvailableStock(any(), any(), any());
  }

  @Test
  @DisplayName("getStockOverview : should throw NotFoundException when book not found")
  void getStockOverview_shouldThrow_whenBookNotFound() {
    when(libraryRepository.existsById(libraryId)).thenReturn(true);
    when(bookRepository.existsById(bookId)).thenReturn(false);

    assertThatThrownBy(
            () -> analyticsService.getStockOverview(libraryId, bookId, "ALL"))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(bookId.toString());

    verify(analyticsRepository, never()).countAvailableStock(any(), any(), any());
  }
}
