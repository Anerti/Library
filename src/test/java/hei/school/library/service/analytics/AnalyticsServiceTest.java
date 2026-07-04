package hei.school.library.service.analytics;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import hei.school.library.entity.enums.BookCopyFormat;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.AnalyticsMapper;
import hei.school.library.repository.dao.AnalyticsRepository;
import hei.school.library.service.AnalyticsService;
import hei.school.library.validator.AnalyticsValidator;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AnalyticsServiceTest {

  @Mock private AnalyticsRepository analyticsRepository;
  @Mock private AnalyticsValidator analyticsValidator;
  private AnalyticsMapper analyticsMapper;
  private AnalyticsService analyticsService;

  private UUID libraryId;
  private UUID bookId;

  @BeforeEach
  void setUp() {
    analyticsMapper = new AnalyticsMapper();
    analyticsService =
        new AnalyticsService(analyticsRepository, analyticsValidator, analyticsMapper);
    libraryId = UUID.randomUUID();
    bookId = UUID.randomUUID();
  }

  @Test
  @DisplayName("getStockOverview : should return total stock for all formats")
  void getStockOverview_shouldReturnTotal_whenFormatIsAll() {
    when(analyticsRepository.checkBookCopyLink(libraryId, bookId)).thenReturn(new Object());
    when(analyticsRepository.countAvailableStock(bookId, null, libraryId)).thenReturn(20L);

    var result = analyticsService.getStockOverview(libraryId, bookId, "ALL");

    assertThat(result.getBookId()).isEqualTo(bookId);
    assertThat(result.getTotal()).isEqualTo(20);
    assertThat(result.getByFormat()).isNull();
  }

  @Test
  @DisplayName("getStockOverview : should return stock for a specific format")
  void getStockOverview_shouldReturnStock_forSpecificFormat() {
    when(analyticsRepository.checkBookCopyLink(libraryId, bookId)).thenReturn(new Object());
    when(analyticsRepository.countAvailableStock(bookId, BookCopyFormat.PAPERBACK, libraryId))
        .thenReturn(7L);

    var result = analyticsService.getStockOverview(libraryId, bookId, "PAPERBACK");

    assertThat(result.getBookId()).isEqualTo(bookId);
    assertThat(result.getTotal()).isEqualTo(7);
    assertThat(result.getByFormat()).isEqualTo(BookCopyFormat.PAPERBACK);
  }

  @Test
  @DisplayName("getStockOverview : should return zero when no copies available")
  void getStockOverview_shouldReturnZero_whenNoCopies() {
    when(analyticsRepository.checkBookCopyLink(libraryId, bookId)).thenReturn(new Object());
    when(analyticsRepository.countAvailableStock(bookId, BookCopyFormat.POCKET, libraryId))
        .thenReturn(0L);

    var result = analyticsService.getStockOverview(libraryId, bookId, "POCKET");

    assertThat(result.getTotal()).isZero();
  }

  @Test
  @DisplayName("getStockOverview : should throw NotFoundException when no book_copy links library and book")
  void getStockOverview_shouldThrow_whenNoLinkExists() {
    when(analyticsRepository.checkBookCopyLink(libraryId, bookId)).thenReturn(null);

    assertThatThrownBy(
            () -> analyticsService.getStockOverview(libraryId, bookId, "ALL"))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(libraryId.toString())
        .hasMessageContaining(bookId.toString());

    verify(analyticsRepository, never()).countAvailableStock(any(), any(), any());
  }
}
