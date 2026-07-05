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
  @DisplayName("getLowStockBooks : should return format rows with stock <= threshold")
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

    when(analyticsRepository.findLowStockBooks(libraryId, bookId, 3, null))
        .thenReturn(rows);
    when(analyticsMapper.toLowStockResponse(row)).thenReturn(response);

    List<BookLowStockResponse> result =
        analyticsService.getLowStockBooks(libraryId, bookId, 3, null);

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getStock()).isEqualTo(2L);
    assertThat(result.getFirst().getFormat()).isEqualTo(BookCopyFormat.PAPERBACK);
    verify(analyticsValidator).validateThreshold(3);
    verify(analyticsValidator).validateFormat(null);
  }

  @Test
  @DisplayName("getLowStockBooks : should return empty list when no low stock rows")
  void getLowStockBooks_shouldReturnEmptyList_whenNoLowStock() {
    when(analyticsRepository.findLowStockBooks(libraryId, bookId, 3, null))
        .thenReturn(List.of());

    List<BookLowStockResponse> result =
        analyticsService.getLowStockBooks(libraryId, bookId, 3, null);

    assertThat(result).isEmpty();
    verify(analyticsValidator).validateThreshold(3);
    verify(analyticsValidator).validateFormat(null);
  }

  @Test
  @DisplayName(
      "getLowStockBooks : should throw UnprocessableEntityException when threshold is negative")
  void getLowStockBooks_shouldThrow_whenThresholdIsNegative() {
    doThrow(new UnprocessableEntityException("Threshold must be greater than 0"))
        .when(analyticsValidator)
        .validateThreshold(-1);

    assertThatThrownBy(
            () -> analyticsService.getLowStockBooks(libraryId, bookId, -1, null))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("Threshold must be greater than 0");

    verify(analyticsRepository, never())
        .findLowStockBooks(any(), any(), anyInt(), any());
  }

  @Test
  @DisplayName("getLowStockBooks : should return rows with stock = 0")
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

    when(analyticsRepository.findLowStockBooks(libraryId, bookId, 3, null))
        .thenReturn(rows);
    when(analyticsMapper.toLowStockResponse(row)).thenReturn(response);

    List<BookLowStockResponse> result =
        analyticsService.getLowStockBooks(libraryId, bookId, 3, null);

    assertThat(result.getFirst().getStock()).isEqualTo(0L);
    verify(analyticsValidator).validateThreshold(3);
    verify(analyticsValidator).validateFormat(null);
  }

  @Test
  @DisplayName("getLowStockBooks : should throw UnprocessableEntityException when format is invalid")
  void getLowStockBooks_shouldThrow_whenFormatIsInvalid() {
    doThrow(new UnprocessableEntityException("Invalid format"))
        .when(analyticsValidator)
        .validateFormat("INVALID");

    assertThatThrownBy(
            () -> analyticsService.getLowStockBooks(libraryId, bookId, 3, "INVALID"))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("Invalid format");

    verify(analyticsRepository, never())
        .findLowStockBooks(any(), any(), anyInt(), any());
  }
}
