package hei.school.library.service.saleItem;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import hei.school.library.dto.SaleItemRequest;
import hei.school.library.dto.SaleItemResponse;
import hei.school.library.entity.Sale;
import hei.school.library.entity.SaleItem;
import hei.school.library.entity.enums.SaleStatus;
import hei.school.library.exception.ConflictException;
import hei.school.library.exception.NotFoundException;
import hei.school.library.exception.UnprocessableEntityException;
import hei.school.library.mapper.SaleItemMapper;
import hei.school.library.repository.dao.SaleItemRepository;
import hei.school.library.repository.dao.SaleRepository;
import hei.school.library.service.SaleItemService;
import hei.school.library.validator.SaleItemValidator;
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
class SaleItemServiceTest {

  @Mock private SaleItemRepository saleItemRepository;
  @Mock private SaleRepository saleRepository;
  @Mock private SaleItemValidator saleItemValidator;
  private SaleItemMapper saleItemMapper;
  private SaleItemService saleItemService;

  private UUID saleId;
  private UUID bookCopyId;
  private Sale sale;
  private SaleItem saleItem;
  private SaleItemRequest validRequest;

  @BeforeEach
  void setUp() {
    saleItemMapper = new SaleItemMapper();
    saleItemService =
        new SaleItemService(saleItemRepository, saleRepository, saleItemMapper, saleItemValidator);

    saleId = UUID.randomUUID();
    bookCopyId = UUID.randomUUID();

    sale =
        new Sale(
            saleId,
            Instant.now(),
            SaleStatus.BOOKED,
            UUID.randomUUID(),
            UUID.randomUUID(),
            null,
            Instant.now());

    saleItem =
        new SaleItem(
            UUID.randomUUID(), bookCopyId, saleId, 1, BigDecimal.valueOf(25.00), Instant.now());

    validRequest = new SaleItemRequest(bookCopyId, 1, BigDecimal.valueOf(25.00));
  }

  @Test
  @DisplayName("findBySaleId: should return list of sale items")
  void findBySaleId_shouldReturnList() {
    when(saleRepository.findById(saleId)).thenReturn(Optional.of(sale));
    when(saleItemRepository.findBySaleId(saleId)).thenReturn(List.of(saleItem));

    List<SaleItemResponse> result = saleItemService.findBySaleId(saleId);

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getBookCopyId()).isEqualTo(bookCopyId);
    assertThat(result.getFirst().getPrice()).isEqualByComparingTo(BigDecimal.valueOf(25.00));
  }

  @Test
  @DisplayName("findBySaleId: should throw NotFoundException when sale not found")
  void findBySaleId_shouldThrow_whenSaleNotFound() {
    UUID unknownSaleId = UUID.randomUUID();
    when(saleRepository.findById(unknownSaleId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> saleItemService.findBySaleId(unknownSaleId))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  @DisplayName("create: should save and return DTO")
  void create_shouldSaveAndReturnDto() {
    when(saleRepository.findById(saleId)).thenReturn(Optional.of(sale));
    when(saleItemRepository.create(any(), any(), any(), any())).thenReturn(Optional.of(saleItem));

    SaleItemResponse result = saleItemService.create(saleId, validRequest);

    assertThat(result.getBookCopyId()).isEqualTo(bookCopyId);
    assertThat(result.getSaleId()).isEqualTo(saleId);
    verify(saleItemValidator).validateCreate(validRequest);
  }

  @Test
  @DisplayName("create: should throw ConflictException when already exists")
  void create_shouldThrow_whenDuplicate() {
    when(saleRepository.findById(saleId)).thenReturn(Optional.of(sale));
    when(saleItemRepository.create(any(), any(), any(), any())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> saleItemService.create(saleId, validRequest))
        .isInstanceOf(ConflictException.class);
  }

  @Test
  @DisplayName("create: should throw NotFoundException when sale not found")
  void create_shouldThrow_whenSaleNotFound() {
    UUID unknownSaleId = UUID.randomUUID();
    when(saleRepository.findById(unknownSaleId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> saleItemService.create(unknownSaleId, validRequest))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  @DisplayName("create: should throw UnprocessableEntityException when price is null")
  void create_shouldThrow_whenPriceNull() {
    SaleItemRequest invalidRequest = new SaleItemRequest(bookCopyId, 1, null);

    doThrow(new UnprocessableEntityException("price is required."))
        .when(saleItemValidator)
        .validateCreate(invalidRequest);

    assertThatThrownBy(() -> saleItemService.create(saleId, invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("price is required.");
  }
}
