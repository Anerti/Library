package hei.school.library.service.customer;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import hei.school.library.dto.CustomerResponse;
import hei.school.library.dto.CustomerUpdateRequest;
import hei.school.library.entity.Customer;
import hei.school.library.exception.NotFoundException;
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
class PatchCustomersServiceTest {

  @Mock private CustomerRepository customerRepository;
  @Mock private CustomerValidator customerValidator;
  @Mock private DataValidator dataValidator;
  private CustomerService customerService;

  private UUID existingId;
  private UUID unknownId;
  private Customer customer;

  @BeforeEach
  void setUp() {
    CustomerMapper customerMapper = new CustomerMapper();
    customerService =
        new CustomerService(customerRepository, customerMapper, dataValidator, customerValidator);

    existingId = UUID.randomUUID();
    unknownId = UUID.randomUUID();
    customer =
        new Customer(
            existingId,
            "Dupont",
            "Marie",
            LocalDate.of(1995, 3, 10),
            "marie@mail.com",
            "+261331234567",
            Instant.now(),
            Instant.now());
  }

  @Test
  @DisplayName("update: should update and return DTO")
  void update_shouldUpdateAndReturnDto() {
    CustomerUpdateRequest request =
        new CustomerUpdateRequest(null, "Marie-Claire", null, null, null);

    Customer updated =
        new Customer(
            existingId,
            "Dupont",
            "Marie-Claire",
            LocalDate.of(1995, 3, 10),
            "marie@mail.com",
            "+261331234567",
            Instant.now(),
            Instant.now());

    when(customerRepository.findById(existingId)).thenReturn(Optional.of(customer));
    when(customerRepository.save(any(Customer.class))).thenReturn(updated);

    CustomerResponse result = customerService.update(existingId, request);

    assertThat(result.getFirstName()).isEqualTo("Marie-Claire");
    verify(customerValidator).validateUpdate(request);
  }

  @Test
  @DisplayName("update: should throw NotFoundException when customer not found")
  void update_shouldThrow_whenNotFound() {
    when(customerRepository.findById(unknownId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> customerService.update(unknownId, new CustomerUpdateRequest()))
        .isInstanceOf(NotFoundException.class);

    verify(customerRepository, never()).save(any(Customer.class));
  }

  @Test
  @DisplayName("update: should throw UnprocessableEntityException when all fields are null")
  void update_shouldThrow_whenAllFieldsNull() {
    CustomerUpdateRequest emptyRequest = new CustomerUpdateRequest();

    doThrow(new UnprocessableEntityException("At least one field is required."))
        .when(customerValidator)
        .validateUpdate(emptyRequest);

    assertThatThrownBy(() -> customerService.update(existingId, emptyRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("At least one field is required.");
  }

  @Test
  @DisplayName("update: should throw UnprocessableEntityException when birthDate is in the future")
  void update_shouldThrow_whenBirthDateInFuture() {
    CustomerUpdateRequest request =
        new CustomerUpdateRequest(null, null, LocalDate.now().plusDays(1), null, null);

    doThrow(new UnprocessableEntityException("birthDate cannot be in the future."))
        .when(customerValidator)
        .validateUpdate(request);

    assertThatThrownBy(() -> customerService.update(existingId, request))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("birthDate cannot be in the future.");
  }
}
