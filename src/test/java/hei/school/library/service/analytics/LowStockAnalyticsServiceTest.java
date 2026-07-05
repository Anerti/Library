package hei.school.library.service.analytics;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import hei.school.library.dto.BookLowStockResponse;
import hei.school.library.entity.enums.BookCopyFormat;
import hei.school.library.exception.UnprocessableEntityException;
import hei.school.library.mapper.AnalyticsMapper;
import hei.school.library.repository.dao.AnalyticsRepository;
import hei.school.library.service.AnalyticsService;
import hei.school.library.validator.AnalyticsValidator;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LowStockAnalyticsServiceTest {

  @Mock private AnalyticsRepository analyticsRepository;
  @Mock private AnalyticsValidator analyticsValidator;
  @Mock private AnalyticsMapper analyticsMapper;

  @InjectMocks private AnalyticsService analyticsService;

  private UUID libraryId;
  private UUID bookId;

  @BeforeEach
  void setUp() {
    libraryId = UUID.randomUUID();
    bookId = UUID.randomUUID();
  }

  @Test
  @DisplayName("getLowStockBooks : should return books with stock <= threshold")
  void getLowStockBooks_shouldReturnLowStockBooks() {
    Object[] row = {bookId.toString(), libraryId.toString(), "PAPERBACK", 2L};

    BookLowStockResponse response =
        BookLowStockResponse.builder()
            .bookId(bookId)
            .libraryId(libraryId)
            .format(BookCopyFormat.PAPERBACK)
            .stock(2L)
            .build();

    List<Object[]> rows = new ArrayList<>();
    rows.add(row);

    when(analyticsRepository.findLowStockBooks(libraryId, 3)).thenReturn(rows);
    when(analyticsMapper.toLowStockResponse(row)).thenReturn(response);

    List<BookLowStockResponse> result = analyticsService.getLowStockBooks(libraryId, 3);

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getStock()).isEqualTo(2L);
    assertThat(result.getFirst().getFormat()).isEqualTo(BookCopyFormat.PAPERBACK);
  }

  @Test
  @DisplayName("getLowStockBooks : should return empty list when no low stock books")
  void getLowStockBooks_shouldReturnEmptyList_whenNoLowStock() {
    when(analyticsRepository.findLowStockBooks(libraryId, 3)).thenReturn(List.of());

    List<BookLowStockResponse> result = analyticsService.getLowStockBooks(libraryId, 3);

    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName(
      "getLowStockBooks : should throw UnprocessableEntityException when threshold is negative")
  void getLowStockBooks_shouldThrow_whenThresholdIsNegative() {
    assertThatThrownBy(() -> analyticsService.getLowStockBooks(libraryId, -1))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("Threshold must be positive");

    verify(analyticsRepository, never()).findLowStockBooks(any(), anyInt());
  }

  @Test
  @DisplayName("getLowStockBooks : should return books with stock = 0")
  void getLowStockBooks_shouldReturnBooks_withZeroStock() {
    Object[] row = {bookId.toString(), libraryId.toString(), "HARDCOVER", 0L};

    BookLowStockResponse response =
        BookLowStockResponse.builder()
            .bookId(bookId)
            .libraryId(libraryId)
            .format(BookCopyFormat.HARDCOVER)
            .stock(0L)
            .build();

    List<Object[]> rows = new ArrayList<>();
    rows.add(row);

    when(analyticsRepository.findLowStockBooks(libraryId, 3)).thenReturn(rows);
    when(analyticsMapper.toLowStockResponse(row)).thenReturn(response);

    List<BookLowStockResponse> result = analyticsService.getLowStockBooks(libraryId, 3);

    assertThat(result.getFirst().getStock()).isEqualTo(0L);
  }
}
