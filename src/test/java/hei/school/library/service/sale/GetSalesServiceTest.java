package hei.school.library.service.sale;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import hei.school.library.dto.CustomerResponse;
import hei.school.library.dto.PageResponse;
import hei.school.library.dto.SaleResponse;
import hei.school.library.entity.Customer;
import hei.school.library.entity.Library;
import hei.school.library.entity.Sale;
import hei.school.library.entity.enums.SaleStatus;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.CustomerMapper;
import hei.school.library.mapper.SaleMapper;
import hei.school.library.repository.dao.CustomerRepository;
import hei.school.library.repository.dao.LibraryRepository;
import hei.school.library.repository.dao.SaleRepository;
import hei.school.library.service.SaleService;
import hei.school.library.validator.SaleValidator;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

@ExtendWith(MockitoExtension.class)
class GetSalesServiceTest {

  @Mock private SaleRepository saleRepository;
  @Mock private CustomerRepository customerRepository;
  @Mock private LibraryRepository libraryRepository;
  @Mock private CustomerMapper customerMapper;
  @Mock private SaleValidator saleValidator;
  private SaleMapper saleMapper;
  private SaleService saleService;

  private UUID libraryId;
  private UUID saleId;
  private UUID customerId;
  private Sale sale;
  private Customer customer;
  private Library library;
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
            saleId, Instant.now(), SaleStatus.SOLD, customerId, libraryId, null, Instant.now());

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
  @DisplayName("findAll: should return page of sales for a library")
  void findAll_shouldReturnPageOfSales() {
    Page<Sale> salePage = new PageImpl<>(List.of(sale));

    when(libraryRepository.findById(libraryId)).thenReturn(Optional.of(library));
    when(saleRepository.findByLibraryId(any(), any(), any(), any(), any(), any()))
        .thenReturn(salePage);
    when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
    when(customerMapper.toResponse(customer)).thenReturn(customerResponse);

    PageResponse<SaleResponse> result =
        saleService.findAll(libraryId, null, null, null, null, 1, 20);

    assertThat(result.getData()).hasSize(1);
    assertThat(result.getData().getFirst().getStatus()).isEqualTo(SaleStatus.SOLD);
    assertThat(result.getData().getFirst().getCustomer().getEmail()).isEqualTo("marie@mail.com");
    assertThat(result.getPagination().getTotal()).isEqualTo(1);
    assertThat(result.getPagination().getPage()).isEqualTo(1);
    assertThat(result.getPagination().getSize()).isEqualTo(20);
  }

  @Test
  @DisplayName("findAll: should throw NotFoundException when library not found")
  void findAll_shouldThrow_whenLibraryNotFound() {
    UUID unknownLibraryId = UUID.randomUUID();
    when(libraryRepository.findById(unknownLibraryId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> saleService.findAll(unknownLibraryId, null, null, null, null, 1, 20))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(unknownLibraryId.toString());
  }

  @Test
  @DisplayName("findById: should return sale when found")
  void findById_shouldReturnSale() {
    when(libraryRepository.findById(libraryId)).thenReturn(Optional.of(library));
    when(saleRepository.findById(saleId)).thenReturn(Optional.of(sale));
    when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
    when(customerMapper.toResponse(customer)).thenReturn(customerResponse);

    SaleResponse result = saleService.findById(libraryId, saleId);

    assertThat(result.getId()).isEqualTo(saleId);
    assertThat(result.getStatus()).isEqualTo(SaleStatus.SOLD);
    assertThat(result.getCustomer().getEmail()).isEqualTo("marie@mail.com");
    assertThat(result.getLibrary().getName()).isEqualTo("Lib A");
  }

  @Test
  @DisplayName("findById: should throw NotFoundException when sale not found")
  void findById_shouldThrow_whenSaleNotFound() {
    UUID unknownSaleId = UUID.randomUUID();
    when(libraryRepository.findById(libraryId)).thenReturn(Optional.of(library));
    when(saleRepository.findById(unknownSaleId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> saleService.findById(libraryId, unknownSaleId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(unknownSaleId.toString());
  }

  @Test
  @DisplayName("findById: should throw NotFoundException when library not found")
  void findById_shouldThrow_whenLibraryNotFound() {
    UUID unknownLibraryId = UUID.randomUUID();
    when(libraryRepository.findById(unknownLibraryId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> saleService.findById(unknownLibraryId, saleId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(unknownLibraryId.toString());
  }
}
