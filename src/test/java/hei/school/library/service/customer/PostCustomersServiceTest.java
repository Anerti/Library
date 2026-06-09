package hei.school.library.service.customer;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import hei.school.library.dto.CustomerRequest;
import hei.school.library.dto.CustomerResponse;
import hei.school.library.entity.Customer;
import hei.school.library.exception.ConflictException;
import hei.school.library.exception.UnprocessableEntityException;
import hei.school.library.mapper.CustomerMapper;
import hei.school.library.repository.dao.CustomerRepository;
import hei.school.library.service.CustomerService;
import hei.school.library.validator.CustomerValidator;
import hei.school.library.validator.DataValidator;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostCustomersServiceTest {

  @Mock private CustomerRepository customerRepository;
  @Mock private CustomerValidator customerValidator;
  @Mock private DataValidator dataValidator;
  private CustomerService customerService;

  private Customer customer;
  private CustomerRequest validRequest;

  @BeforeEach
  void setUp() {
    CustomerMapper customerMapper = new CustomerMapper();
    customerService =
        new CustomerService(customerRepository, customerMapper, dataValidator, customerValidator);

    UUID id = UUID.randomUUID();
    customer =
        new Customer(
            id,
            "Dupont",
            "Marie",
            LocalDate.of(1995, 3, 10),
            "marie@mail.com",
            "+261331234567",
            Instant.now(),
            Instant.now());

    validRequest =
        new CustomerRequest(
            "Dupont", "Marie", LocalDate.of(1995, 3, 10), "marie@mail.com", "+261331234567");
  }

  @Test
  @DisplayName("create: should save and return DTO")
  void create_shouldSaveAndReturnDto() {
    when(customerRepository.create(any(), any(), any(), any(), any()))
        .thenReturn(Optional.of(customer));

    CustomerResponse result = customerService.create(validRequest);

    assertThat(result.getLastName()).isEqualTo("Dupont");
    assertThat(result.getEmail()).isEqualTo("marie@mail.com");
    verify(customerValidator).validateCreate(validRequest);
  }

  @Test
  @DisplayName("create: should throw ConflictException when email already exists")
  void create_shouldThrow_whenDuplicateEmail() {
    when(customerRepository.create(any(), any(), any(), any(), any())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> customerService.create(validRequest))
        .isInstanceOf(ConflictException.class)
        .hasMessageContaining("marie@mail.com");
  }

  @Test
  @DisplayName("create: should throw UnprocessableEntityException when lastName is missing")
  void create_shouldThrow_whenLastNameMissing() {
    CustomerRequest invalidRequest =
        new CustomerRequest(null, "Marie", LocalDate.of(1995, 3, 10), "marie@mail.com", null);

    doThrow(new UnprocessableEntityException("lastName is required."))
        .when(customerValidator)
        .validateCreate(invalidRequest);

    assertThatThrownBy(() -> customerService.create(invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("lastName is required.");
  }

  @Test
  @DisplayName("create: should throw UnprocessableEntityException when birthDate is in the future")
  void create_shouldThrow_whenBirthDateInFuture() {
    CustomerRequest futureRequest =
        new CustomerRequest(
            "Dupont", "Marie", LocalDate.now().plusDays(1), "future@mail.com", null);

    doThrow(new UnprocessableEntityException("birthDate cannot be in the future."))
        .when(customerValidator)
        .validateCreate(futureRequest);

    assertThatThrownBy(() -> customerService.create(futureRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("birthDate cannot be in the future.");
  }
}
