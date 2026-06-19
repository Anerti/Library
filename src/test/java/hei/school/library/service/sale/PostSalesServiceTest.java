package hei.school.library.service.sale;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import hei.school.library.dto.CustomerRequest;
import hei.school.library.dto.SaleRequest;
import hei.school.library.dto.SaleResponse;
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
class PostSalesServiceTest {

  @Mock private SaleRepository saleRepository;
  @Mock private CustomerRepository customerRepository;
  @Mock private LibraryRepository libraryRepository;
  @Mock private SaleValidator saleValidator;
  private SaleService saleService;

  private UUID libraryId;
  private UUID customerId;
  private UUID saleId;
  private Library library;
  private Customer customer;
  private Sale sale;
  private SaleRequest validRequest;

  @BeforeEach
  void setUp() {
    CustomerMapper customerMapper = new CustomerMapper();
    SaleMapper saleMapper = new SaleMapper();
    saleService =
        new SaleService(
            saleRepository,
            customerRepository,
            libraryRepository,
            customerMapper,
            saleMapper,
            saleValidator);

    libraryId = UUID.randomUUID();
    customerId = UUID.randomUUID();
    saleId = UUID.randomUUID();

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

    CustomerRequest customerRequest =
        new CustomerRequest(
            "Dupont", "Marie", LocalDate.of(1995, 3, 10), "marie@mail.com", "+261331234567");
    validRequest = new SaleRequest(null, customerRequest, null);
  }

  @Test
  @DisplayName("create: should save and return DTO")
  void create_shouldSaveAndReturnDto() {
    when(libraryRepository.findById(libraryId)).thenReturn(Optional.of(library));
    when(customerRepository.findByEmail("marie@mail.com")).thenReturn(Optional.of(customer));
    when(saleRepository.create(any(), any(), any(), any(), any())).thenReturn(Optional.of(sale));

    SaleResponse result = saleService.create(libraryId, validRequest);

    assertThat(result.getStatus()).isEqualTo(SaleStatus.BOOKED);
    assertThat(result.getCustomer().getEmail()).isEqualTo("marie@mail.com");
    verify(saleValidator).validateCreate(validRequest);
  }

  @Test
  @DisplayName("create: should create new customer when not found by email")
  void create_shouldCreateCustomer_whenNotFound() {
    when(libraryRepository.findById(libraryId)).thenReturn(Optional.of(library));
    when(customerRepository.findByEmail("marie@mail.com")).thenReturn(Optional.empty());
    when(customerRepository.create(any(), any(), any(), any(), any()))
        .thenReturn(Optional.of(customer));
    when(saleRepository.create(any(), any(), any(), any(), any())).thenReturn(Optional.of(sale));

    SaleResponse result = saleService.create(libraryId, validRequest);

    assertThat(result.getCustomer().getEmail()).isEqualTo("marie@mail.com");
    verify(customerRepository).create(any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("create: should throw NotFoundException when library not found")
  void create_shouldThrow_whenLibraryNotFound() {
    UUID unknownLibraryId = UUID.randomUUID();
    when(libraryRepository.findById(unknownLibraryId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> saleService.create(unknownLibraryId, validRequest))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  @DisplayName("create: should throw UnprocessableEntityException when customer is null")
  void create_shouldThrow_whenCustomerNull() {
    SaleRequest invalidRequest = new SaleRequest(null, null, null);

    doThrow(new UnprocessableEntityException("customer is required."))
        .when(saleValidator)
        .validateCreate(invalidRequest);

    assertThatThrownBy(() -> saleService.create(libraryId, invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("customer is required.");
  }
}
