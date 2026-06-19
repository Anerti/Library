package hei.school.library.service.sale;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import hei.school.library.dto.CustomerResponse;
import hei.school.library.dto.SaleResponse;
import hei.school.library.dto.SaleUpdateRequest;
import hei.school.library.entity.Customer;
import hei.school.library.entity.Library;
import hei.school.library.entity.Sale;
import hei.school.library.entity.enums.SaleStatus;
import hei.school.library.exception.NotFoundException;
import hei.school.library.exception.UnprocessableEntityException;
import hei.school.library.mapper.CustomerMapper;
import hei.school.library.mapper.SaleMapper;
import hei.school.library.repository.dao.CustomerRepository;
import hei.school.library.repository.dao.LibraryRepository;
import hei.school.library.repository.dao.SaleRepository;
import hei.school.library.service.SaleService;
import hei.school.library.validator.SaleValidator;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PatchSalesServiceTest {

  @Mock private SaleRepository saleRepository;
  @Mock private CustomerRepository customerRepository;
  @Mock private LibraryRepository libraryRepository;
  @Mock private SaleValidator saleValidator;
  @Mock private CustomerMapper customerMapper;
  private SaleMapper saleMapper;
  private SaleService saleService;

  private UUID libraryId;
  private UUID saleId;
  private UUID customerId;
  private Library library;
  private Customer customer;
  private Sale sale;
  private CustomerResponse customerResponse;

  @BeforeEach
  void setUp() {
    saleMapper = new SaleMapper();
    saleService =
        new SaleService(
            saleRepository,
            customerRepository,
            libraryRepository,
            customerMapper,
            saleMapper,
            saleValidator);

    libraryId = UUID.randomUUID();
    saleId = UUID.randomUUID();
    customerId = UUID.randomUUID();

    library = new Library(libraryId, "Lib A", "+261341234567", "lib@mail.com", "Antananarivo");
    customer =
        new Customer(
            customerId,
            "Dupont",
            "Marie",
            LocalDate.of(1995, 3, 10),
            "marie@mail.com",
            "+261331234567",
            Instant.now(),
            Instant.now());
    sale =
        new Sale(
            saleId, Instant.now(), SaleStatus.BOOKED, customerId, libraryId, null, Instant.now());

    customerResponse =
        new CustomerResponse(
            customerId,
            "Dupont",
            "Marie",
            LocalDate.of(1995, 3, 10),
            "marie@mail.com",
            "+261331234567",
            Instant.now(),
            Instant.now());
  }

  @Test
  @DisplayName("update: should update status and return DTO")
  void update_shouldUpdateStatusAndReturnDto() {
    SaleUpdateRequest request = new SaleUpdateRequest(SaleStatus.SOLD, null);

    Sale updated =
        new Sale(
            saleId,
            sale.getSaleDate(),
            SaleStatus.SOLD,
            customerId,
            libraryId,
            null,
            Instant.now());

    when(libraryRepository.findById(libraryId)).thenReturn(Optional.of(library));
    when(saleRepository.findById(saleId)).thenReturn(Optional.of(sale));
    when(saleRepository.save(any(Sale.class))).thenReturn(updated);
    when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
    when(customerMapper.toResponse(customer)).thenReturn(customerResponse);

    SaleResponse result = saleService.update(libraryId, saleId, request);

    assertThat(result.getStatus()).isEqualTo(SaleStatus.SOLD);
    verify(saleValidator).validateUpdate(request);
  }

  @Test
  @DisplayName("update: should throw NotFoundException when sale not found")
  void update_shouldThrow_whenSaleNotFound() {
    UUID unknownSaleId = UUID.randomUUID();
    SaleUpdateRequest request = new SaleUpdateRequest(SaleStatus.SOLD, null);

    when(libraryRepository.findById(libraryId)).thenReturn(Optional.of(library));
    when(saleRepository.findById(unknownSaleId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> saleService.update(libraryId, unknownSaleId, request))
        .isInstanceOf(NotFoundException.class);

    verify(saleRepository, never()).save(any(Sale.class));
  }

  @Test
  @DisplayName("update: should throw NotFoundException when library not found")
  void update_shouldThrow_whenLibraryNotFound() {
    UUID unknownLibraryId = UUID.randomUUID();
    SaleUpdateRequest request = new SaleUpdateRequest(SaleStatus.SOLD, null);

    when(libraryRepository.findById(unknownLibraryId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> saleService.update(unknownLibraryId, saleId, request))
        .isInstanceOf(NotFoundException.class);

    verify(saleRepository, never()).save(any(Sale.class));
  }

  @Test
  @DisplayName("update: should throw UnprocessableEntityException when all fields are null")
  void update_shouldThrow_whenAllFieldsNull() {
    SaleUpdateRequest emptyRequest = new SaleUpdateRequest();

    doThrow(new UnprocessableEntityException("At least one field is required."))
        .when(saleValidator)
        .validateUpdate(emptyRequest);

    assertThatThrownBy(() -> saleService.update(libraryId, saleId, emptyRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("At least one field is required.");
  }
}
