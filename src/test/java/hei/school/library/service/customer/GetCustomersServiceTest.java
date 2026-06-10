package hei.school.library.service.customer;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import hei.school.library.dto.CustomerResponse;
import hei.school.library.dto.PageResponse;
import hei.school.library.entity.Customer;
import hei.school.library.exception.UnprocessableEntityException;
import hei.school.library.mapper.CustomerMapper;
import hei.school.library.mapper.PaginationMapper;
import hei.school.library.repository.dao.CustomerRepository;
import hei.school.library.service.CustomerService;
import hei.school.library.validator.DataValidator;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

@ExtendWith(MockitoExtension.class)
class GetCustomersServiceTest {

  @Mock private CustomerRepository customerRepository;
  @Mock private DataValidator dataValidator;
  private CustomerService customerService;
  private Customer customer;

  @BeforeEach
  void setUp() {
    CustomerMapper customerMapper = new CustomerMapper(new PaginationMapper());
    customerService = new CustomerService(customerRepository, customerMapper, dataValidator);

    customer =
        new Customer(
            UUID.randomUUID(),
            "Dupont",
            "Marie",
            LocalDate.of(1995, 3, 10),
            "marie@mail.com",
            "+261331234567",
            Instant.now(),
            Instant.now());
  }

  @Test
  @DisplayName("findAll: should return page of customers when search is null")
  void findAll_shouldReturnPage_whenSearchNull() {
    Page<Customer> page = new PageImpl<>(List.of(customer));
    when(customerRepository.findAll(any(Pageable.class))).thenReturn(page);

    PageResponse<CustomerResponse> result = customerService.findAll(null, 1, 20);

    assertThat(result.getData()).hasSize(1);
    assertThat(result.getData().getFirst().getLastName()).isEqualTo("Dupont");
    assertThat(result.getPagination().getTotal()).isEqualTo(1);
    assertThat(result.getPagination().getPage()).isEqualTo(1);
    assertThat(result.getPagination().getSize()).isEqualTo(20);
    verify(customerRepository).findAll(any(Pageable.class));
  }

  @Test
  @DisplayName("findAll: should search by keyword")
  void findAll_shouldSearch_whenKeywordGiven() {
    Page<Customer> page = new PageImpl<>(List.of(customer));
    when(customerRepository.findBySearch(any(), any(Pageable.class))).thenReturn(page);

    PageResponse<CustomerResponse> result = customerService.findAll("Dupont", 1, 20);

    assertThat(result.getData()).hasSize(1);
    assertThat(result.getData().getFirst().getLastName()).isEqualTo("Dupont");
    verify(customerRepository).findBySearch("Dupont", PageRequest.of(0, 20));
  }

  @Test
  @DisplayName("findAll: should not expose password in response")
  void findAll_shouldNotExposePassword() {
    Page<Customer> page = new PageImpl<>(List.of(customer));
    when(customerRepository.findAll(any(Pageable.class))).thenReturn(page);

    PageResponse<CustomerResponse> result = customerService.findAll(null, 1, 20);

    assertThat(result.getData().getFirst()).isInstanceOf(CustomerResponse.class);
  }

  @Test
  @DisplayName(
      "findAll: should throw UnprocessableEntityException when search contains invalid characters")
  void findAll_shouldThrow_whenSearchInvalid() {
    String invalidSearch = "customer!</>";

    doThrow(
            new UnprocessableEntityException(
                "Field 'search' contains invalid characters. Only letters (a-z, A-Z), digits (0-9),"
                    + " and @ ' . - _ are allowed."))
        .when(dataValidator)
        .SearchString("search", invalidSearch);

    assertThatThrownBy(() -> customerService.findAll(invalidSearch, 1, 20))
        .isInstanceOf(UnprocessableEntityException.class);
  }
}
