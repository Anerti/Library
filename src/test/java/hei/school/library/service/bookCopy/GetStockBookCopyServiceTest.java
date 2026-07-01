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
import hei.school.library.repository.dao.StockRepository;
import hei.school.library.service.BookCopyService;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GetStockBookCopyServiceTest {

  @Mock
  private StockRepository stockRepository;
  @Mock
  private BookRepository bookRepository;
  @Mock
  private LibraryRepository libraryRepository;
  @Mock
  private BookCopyMapper bookCopyMapper;
  @Mock
  private PaginationMapper paginationMapper;

  @InjectMocks
  private BookCopyService bookCopyService;

  private UUID bookId;
  private UUID libraryId;

  @BeforeEach
  void setUp() {
    bookId = UUID.randomUUID();
    libraryId = UUID.randomUUID();
  }

  @Test
  @DisplayName("getStock : should calculate stock as received minus sold")
  void getStock_shouldReturnDifference_receivedMinusSold() {
    when(bookRepository.existsById(bookId)).thenReturn(true);
    when(stockRepository.calculateStock(
            bookId, "PAPERBACK", null))
            .thenReturn(List.of(38L, 25L));

    long result = bookCopyService.getStock(bookId, BookCopyFormat.PAPERBACK, null);

    assertThat(result).isEqualTo(63L);
  }

  @Test
  @DisplayName("getStock : should return zero when all copies are sold")
  void getStock_shouldReturnZero_whenAllSold() {
    when(bookRepository.existsById(bookId)).thenReturn(true);
    when(stockRepository.calculateStock(bookId, null, null))
            .thenReturn(List.of(0L));

    long result = bookCopyService.getStock(bookId, null, null);

    assertThat(result).isEqualTo(0L);
  }

  @Test
  @DisplayName("getStock : should return zero when no copies received")
  void getStock_shouldReturnZero_whenNoCopiesReceived() {
    when(bookRepository.existsById(bookId)).thenReturn(true);
    when(stockRepository.calculateStock(bookId, null, null))
            .thenReturn(List.of());

    long result = bookCopyService.getStock(bookId, null, null);

    assertThat(result).isEqualTo(0L);
  }
}
