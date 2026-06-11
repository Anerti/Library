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
import hei.school.library.mapper.PaginationMapper;
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
    CustomerMapper customerMapper = new CustomerMapper(new PaginationMapper());
    customerService = new CustomerService(customerRepository, customerMapper, dataValidator);

    UUID id = UUID.randomUUID();
    customer =
        new Customer(
            UUID.randomUUID(),
            id,
            "Dupont",
            "Marie",
            LocalDate.of(1995, 3, 10),
            "marie@mail.com",
            "+261****4567",
            "+261331234567",
            Instant.now(),
            Instant.now());

    validRequest =
        new CustomerRequest(
            "Dupont", "Marie", LocalDate.of(1995, 3, 10), "marie@mail.com", "+261****4567");
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
    verify(dataValidator).validateCustomer(validRequest);
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
        .when(dataValidator)
        .validateCustomer(invalidRequest);
        .when(customerValidator)
        .validateCreate(invalidRequest);

    assertThatThrownBy(() -> customerService.create(invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("lastName is required.");
  }

  @Test
  @DisplayName("create: should throw UnprocessableEntityException when lastName is blank")
  void create_shouldThrow_whenLastNameBlank() {
    CustomerRequest invalidRequest =
        new CustomerRequest("", "Marie", LocalDate.of(1995, 3, 10), "marie@mail.com", null);

    doThrow(new UnprocessableEntityException("lastName is required."))
        .when(dataValidator)
        .validateCustomer(invalidRequest);

    assertThatThrownBy(() -> customerService.create(invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("lastName is required.");
  }

  @Test
  @DisplayName("create: should throw UnprocessableEntityException when lastName contains numbers")
  void create_shouldThrow_whenLastNameHasInvalidChars() {
    CustomerRequest invalidRequest =
        new CustomerRequest(
            "Dupont123", "Marie", LocalDate.of(1995, 3, 10), "marie@mail.com", null);

    doThrow(new UnprocessableEntityException("lastName field contain forbidden characters."))
        .when(dataValidator)
        .validateCustomer(invalidRequest);

    assertThatThrownBy(() -> customerService.create(invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("forbidden");
  }

  @Test
  @DisplayName(
      "create: should throw UnprocessableEntityException when lastName exceeds 100 characters")
  void create_shouldThrow_whenLastNameTooLong() {
    CustomerRequest invalidRequest =
        new CustomerRequest(
            "D".repeat(101), "Marie", LocalDate.of(1995, 3, 10), "marie@mail.com", null);

    doThrow(new UnprocessableEntityException("lastName cannot be longer than 100 characters."))
        .when(dataValidator)
        .validateCustomer(invalidRequest);

    assertThatThrownBy(() -> customerService.create(invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("100 characters");
  }

  @Test
  @DisplayName("create: should throw UnprocessableEntityException when firstName is missing")
  void create_shouldThrow_whenFirstNameMissing() {
    CustomerRequest invalidRequest =
        new CustomerRequest("Dupont", null, LocalDate.of(1995, 3, 10), "marie@mail.com", null);

    doThrow(new UnprocessableEntityException("firstName is required."))
        .when(dataValidator)
        .validateCustomer(invalidRequest);

    assertThatThrownBy(() -> customerService.create(invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("firstName is required.");
  }

  @Test
  @DisplayName("create: should throw UnprocessableEntityException when firstName is blank")
  void create_shouldThrow_whenFirstNameBlank() {
    CustomerRequest invalidRequest =
        new CustomerRequest("Dupont", "", LocalDate.of(1995, 3, 10), "marie@mail.com", null);

    doThrow(new UnprocessableEntityException("firstName is required."))
        .when(dataValidator)
        .validateCustomer(invalidRequest);

    assertThatThrownBy(() -> customerService.create(invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("firstName is required.");
  }

  @Test
  @DisplayName("create: should throw UnprocessableEntityException when firstName contains numbers")
  void create_shouldThrow_whenFirstNameHasInvalidChars() {
    CustomerRequest invalidRequest =
        new CustomerRequest(
            "Dupont", "Marie123", LocalDate.of(1995, 3, 10), "marie@mail.com", null);

    doThrow(new UnprocessableEntityException("firstName field contain forbidden characters."))
        .when(dataValidator)
        .validateCustomer(invalidRequest);

    assertThatThrownBy(() -> customerService.create(invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("forbidden");
  }

  @Test
  @DisplayName(
      "create: should throw UnprocessableEntityException when firstName exceeds 100 characters")
  void create_shouldThrow_whenFirstNameTooLong() {
    CustomerRequest invalidRequest =
        new CustomerRequest(
            "Dupont", "M".repeat(101), LocalDate.of(1995, 3, 10), "marie@mail.com", null);

    doThrow(new UnprocessableEntityException("firstName cannot be longer than 100 characters."))
        .when(dataValidator)
        .validateCustomer(invalidRequest);

    assertThatThrownBy(() -> customerService.create(invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("100 characters");
  }

  @Test
  @DisplayName("create: should throw UnprocessableEntityException when email has invalid format")
  void create_shouldThrow_whenEmailInvalidFormat() {
    CustomerRequest invalidRequest =
        new CustomerRequest("Dupont", "Marie", LocalDate.of(1995, 3, 10), "not-an-email", null);

    doThrow(new UnprocessableEntityException("Invalid email format"))
        .when(dataValidator)
        .validateCustomer(invalidRequest);

    assertThatThrownBy(() -> customerService.create(invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("Invalid email format");
  }

  @Test
  @DisplayName(
      "create: should throw UnprocessableEntityException when email exceeds 100 characters")
  void create_shouldThrow_whenEmailTooLong() {
    CustomerRequest invalidRequest =
        new CustomerRequest(
            "Dupont", "Marie", LocalDate.of(1995, 3, 10), "m".repeat(90) + "@mail.com", null);

    doThrow(new UnprocessableEntityException("email cannot be longer than 100 characters."))
        .when(dataValidator)
        .validateCustomer(invalidRequest);

    assertThatThrownBy(() -> customerService.create(invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("100 characters");
  }

  @Test
  @DisplayName(
      "create: should throw UnprocessableEntityException when email contains forbidden characters")
  void create_shouldThrow_whenEmailHasInvalidChars() {
    CustomerRequest invalidRequest =
        new CustomerRequest("Dupont", "Marie", LocalDate.of(1995, 3, 10), "marie @mail.com", null);

    doThrow(
            new UnprocessableEntityException(
                "Invalid input for email: 'marie @mail.com' only a-zA-Z0-9@_.- characters are"
                    + " allowed."))
        .when(dataValidator)
        .validateCustomer(invalidRequest);

    assertThatThrownBy(() -> customerService.create(invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("Invalid input for email");
  }

  @Test
  @DisplayName("create: should throw UnprocessableEntityException when email is blank")
  void create_shouldThrow_whenEmailBlank() {
    CustomerRequest invalidRequest =
        new CustomerRequest("Dupont", "Marie", LocalDate.of(1995, 3, 10), "", null);

    doThrow(new UnprocessableEntityException("Invalid input for email"))
        .when(dataValidator)
        .validateCustomer(invalidRequest);

    assertThatThrownBy(() -> customerService.create(invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("Invalid input for email");
  }

  @Test
  @DisplayName("create: should throw UnprocessableEntityException when birthDate is null")
  void create_shouldThrow_whenBirthDateNull() {
    CustomerRequest invalidRequest =
        new CustomerRequest("Dupont", "Marie", null, "marie@mail.com", null);

    doThrow(new UnprocessableEntityException("birthDate is required."))
        .when(dataValidator)
        .validateCustomer(invalidRequest);

    assertThatThrownBy(() -> customerService.create(invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("birthDate is required.");
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
        .when(dataValidator)
        .validateCustomer(futureRequest);

    assertThatThrownBy(() -> customerService.create(futureRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("birthDate cannot be in the future.");
  }
}
