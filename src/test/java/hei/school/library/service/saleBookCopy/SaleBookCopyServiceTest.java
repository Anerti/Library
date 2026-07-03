package hei.school.library.service.saleBookCopy;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import hei.school.library.dto.SaleBookCopyRequest;
import hei.school.library.dto.SaleBookCopyResponse;
import hei.school.library.entity.BookCopy;
import hei.school.library.entity.Library;
import hei.school.library.entity.Sale;
import hei.school.library.entity.SaleBookCopy;
import hei.school.library.entity.User;
import hei.school.library.entity.enums.SaleStatus;
import hei.school.library.exception.ConflictException;
import hei.school.library.exception.NotFoundException;
import hei.school.library.exception.UnprocessableEntityException;
import hei.school.library.mapper.SaleBookCopyMapper;
import hei.school.library.repository.dao.SaleBookCopyRepository;
import hei.school.library.repository.dao.SaleRepository;
import hei.school.library.service.SaleBookCopyService;
import hei.school.library.validator.SaleBookCopyValidator;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SaleBookCopyServiceTest {

  @Mock private SaleBookCopyRepository saleBookCopyRepository;
  @Mock private SaleRepository saleRepository;
  @Mock private SaleBookCopyValidator saleBookCopyValidator;
  private SaleBookCopyMapper saleBookCopyMapper;
  private SaleBookCopyService saleBookCopyService;

  private UUID saleId;
  private UUID bookCopyId;
  private Sale sale;
  private SaleBookCopy saleBookCopy;
  private SaleBookCopyRequest validRequest;
  private BookCopy bookCopy;
  private Sale itemSale;

  @BeforeEach
  void setUp() {
    saleBookCopyMapper = new SaleBookCopyMapper();
    saleBookCopyService =
        new SaleBookCopyService(saleBookCopyRepository, saleRepository, saleBookCopyMapper, saleBookCopyValidator);

    saleId = UUID.randomUUID();
    bookCopyId = UUID.randomUUID();

    User saleUser = new User();
    saleUser.setId(UUID.randomUUID());
    Library saleLibrary = new Library();
    saleLibrary.setId(UUID.randomUUID());

    sale =
        new Sale(
            saleId,
            Instant.now(),
            SaleStatus.BOOKED,
            saleUser,
            saleLibrary,
            null,
            Instant.now());

    bookCopy = new BookCopy();
    bookCopy.setId(bookCopyId);
    itemSale = new Sale();
    itemSale.setId(saleId);

    saleBookCopy =
        new SaleBookCopy(
            UUID.randomUUID(), bookCopy, itemSale, 1, BigDecimal.valueOf(25.00), Instant.now());

    validRequest = new SaleBookCopyRequest(bookCopyId, 1, BigDecimal.valueOf(25.00));
  }

  @Test
  @DisplayName("findBySaleId: should return list of sale items")
  void findBySaleId_shouldReturnList() {
    when(saleRepository.findById(saleId)).thenReturn(Optional.of(sale));
    when(saleBookCopyRepository.findBySaleId(saleId)).thenReturn(List.of(saleBookCopy));

    List<SaleBookCopyResponse> result = saleBookCopyService.findBySaleId(saleId);

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getBookCopyId()).isEqualTo(bookCopyId);
    assertThat(result.getFirst().getPrice()).isEqualByComparingTo(BigDecimal.valueOf(25.00));
  }

  @Test
  @DisplayName("findBySaleId: should throw NotFoundException when sale not found")
  void findBySaleId_shouldThrow_whenSaleNotFound() {
    UUID unknownSaleId = UUID.randomUUID();
    when(saleRepository.findById(unknownSaleId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> saleBookCopyService.findBySaleId(unknownSaleId))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  @DisplayName("create: should save and return DTO")
  void create_shouldSaveAndReturnDto() {
    when(saleRepository.findById(saleId)).thenReturn(Optional.of(sale));
    when(saleBookCopyRepository.create(any(), any(), any(), any())).thenReturn(Optional.of(saleBookCopy));

    SaleBookCopyResponse result = saleBookCopyService.create(saleId, validRequest);

    assertThat(result.getBookCopyId()).isEqualTo(bookCopyId);
    assertThat(result.getSaleId()).isEqualTo(saleId);
    verify(saleBookCopyValidator).validateCreate(validRequest);
  }

  @Test
  @DisplayName("create: should throw ConflictException when already exists")
  void create_shouldThrow_whenDuplicate() {
    when(saleRepository.findById(saleId)).thenReturn(Optional.of(sale));
    when(saleBookCopyRepository.create(any(), any(), any(), any())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> saleBookCopyService.create(saleId, validRequest))
        .isInstanceOf(ConflictException.class);
  }

  @Test
  @DisplayName("create: should throw NotFoundException when sale not found")
  void create_shouldThrow_whenSaleNotFound() {
    UUID unknownSaleId = UUID.randomUUID();
    when(saleRepository.findById(unknownSaleId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> saleBookCopyService.create(unknownSaleId, validRequest))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  @DisplayName("create: should throw UnprocessableEntityException when price is null")
  void create_shouldThrow_whenPriceNull() {
    SaleBookCopyRequest invalidRequest = new SaleBookCopyRequest(bookCopyId, 1, null);

    doThrow(new UnprocessableEntityException("price is required."))
        .when(saleBookCopyValidator)
        .validateCreate(invalidRequest);

    assertThatThrownBy(() -> saleBookCopyService.create(saleId, invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("price is required.");
  }

  @Test
  @DisplayName("delete: should delete when sale item exists")
  void delete_shouldDelete_whenExists() {
    when(saleRepository.findById(saleId)).thenReturn(Optional.of(sale));
    when(saleBookCopyRepository.delete(saleId, bookCopyId)).thenReturn(Optional.of(bookCopyId));

    saleBookCopyService.delete(saleId, bookCopyId);

    verify(saleBookCopyRepository).delete(saleId, bookCopyId);
  }

  @Test
  @DisplayName("delete: should throw NotFoundException when sale item not found")
  void delete_shouldThrow_whenNotFound() {
    when(saleRepository.findById(saleId)).thenReturn(Optional.of(sale));
    when(saleBookCopyRepository.delete(saleId, bookCopyId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> saleBookCopyService.delete(saleId, bookCopyId))
        .isInstanceOf(NotFoundException.class);
  }
}
