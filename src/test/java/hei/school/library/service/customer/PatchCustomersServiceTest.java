package hei.school.library.service.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import hei.school.library.dto.CustomerResponse;
import hei.school.library.dto.CustomerUpdateRequest;
import hei.school.library.entity.Customer;
import hei.school.library.exception.NotFoundException;
import hei.school.library.exception.UnprocessableEntityException;
import hei.school.library.mapper.CustomerMapper;
import hei.school.library.mapper.PaginationMapper;
import hei.school.library.repository.dao.CustomerRepository;
import hei.school.library.service.CustomerService;
import hei.school.library.validator.DataValidator;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PatchCustomersServiceTest {

  @Mock private CustomerRepository customerRepository;

  private CustomerService customerService;

  private UUID existingId;
  private Customer customer;

  @BeforeEach
  void setUp() {
    customerService =
        new CustomerService(
            customerRepository, new CustomerMapper(new PaginationMapper()), new DataValidator());

    existingId = UUID.randomUUID();
    customer =
        new Customer(
            existingId,
            "Dupont",
            "Marie",
            LocalDate.of(1995, 3, 10),
            "marie@mail.com",
            "+261****4567",
            Instant.now(),
            Instant.now());
  }

  @Test
  @DisplayName("update: should update firstName only and return DTO")
  void update_shouldUpdateFirstNameOnly() {
    CustomerUpdateRequest request =
        new CustomerUpdateRequest(null, "Marie Claire", null, null, null);

    when(customerRepository.findById(existingId)).thenReturn(Optional.of(customer));
    when(customerRepository.save(any(Customer.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    CustomerResponse result = customerService.update(existingId, request);

    assertThat(result.getFirstName()).isEqualTo("Marie Claire");
    assertThat(result.getLastName()).isEqualTo("Dupont");
    assertThat(result.getEmail()).isEqualTo("marie@mail.com");
    verify(customerRepository).findById(existingId);
    verify(customerRepository).save(customer);
  }

  @Test
  @DisplayName("update: should update lastName only and return DTO")
  void update_shouldUpdateLastNameOnly() {
    CustomerUpdateRequest request = new CustomerUpdateRequest("Martin", null, null, null, null);

    when(customerRepository.findById(existingId)).thenReturn(Optional.of(customer));
    when(customerRepository.save(any(Customer.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    CustomerResponse result = customerService.update(existingId, request);

    assertThat(result.getLastName()).isEqualTo("Martin");
    assertThat(result.getFirstName()).isEqualTo("Marie");
    assertThat(result.getEmail()).isEqualTo("marie@mail.com");
  }

  @Test
  @DisplayName("update: should update email only and return DTO")
  void update_shouldUpdateEmailOnly() {
    CustomerUpdateRequest request =
        new CustomerUpdateRequest(null, null, null, "new@mail.com", null);

    when(customerRepository.findById(existingId)).thenReturn(Optional.of(customer));
    when(customerRepository.save(any(Customer.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    CustomerResponse result = customerService.update(existingId, request);

    assertThat(result.getEmail()).isEqualTo("new@mail.com");
    assertThat(result.getLastName()).isEqualTo("Dupont");
  }

  @Test
  @DisplayName("update: should update phone only and return DTO")
  void update_shouldUpdatePhoneOnly() {
    CustomerUpdateRequest request =
        new CustomerUpdateRequest(null, null, null, null, "+261****0000");

    when(customerRepository.findById(existingId)).thenReturn(Optional.of(customer));
    when(customerRepository.save(any(Customer.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    CustomerResponse result = customerService.update(existingId, request);

    assertThat(result.getPhone()).isEqualTo("+261****0000");
    assertThat(result.getLastName()).isEqualTo("Dupont");
  }

  @Test
  @DisplayName("update: should update birthDate only and return DTO")
  void update_shouldUpdateBirthDateOnly() {
    LocalDate newBirthDate = LocalDate.of(1990, 7, 15);
    CustomerUpdateRequest request = new CustomerUpdateRequest(null, null, newBirthDate, null, null);

    when(customerRepository.findById(existingId)).thenReturn(Optional.of(customer));
    when(customerRepository.save(any(Customer.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    CustomerResponse result = customerService.update(existingId, request);

    assertThat(result.getBirthDate()).isEqualTo(newBirthDate);
  }

  @Test
  @DisplayName("update: should update all fields at once")
  void update_shouldUpdateAllFields() {
    CustomerUpdateRequest request =
        new CustomerUpdateRequest(
            "Martin", "Jean", LocalDate.of(1988, 1, 1), "jean@mail.com", "+261****9999");

    when(customerRepository.findById(existingId)).thenReturn(Optional.of(customer));
    when(customerRepository.save(any(Customer.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    CustomerResponse result = customerService.update(existingId, request);

    assertThat(result.getLastName()).isEqualTo("Martin");
    assertThat(result.getFirstName()).isEqualTo("Jean");
    assertThat(result.getBirthDate()).isEqualTo(LocalDate.of(1988, 1, 1));
    assertThat(result.getEmail()).isEqualTo("jean@mail.com");
    assertThat(result.getPhone()).isEqualTo("+261****9999");
  }

  @Test
  @DisplayName("update: should throw NotFoundException when customer not found")
  void update_shouldThrow_whenNotFound() {
    UUID unknownId = UUID.randomUUID();
    when(customerRepository.findById(unknownId)).thenReturn(Optional.empty());

    assertThatThrownBy(
            () ->
                customerService.update(
                    unknownId, new CustomerUpdateRequest("test", null, null, null, null)))
        .isInstanceOf(NotFoundException.class);

    verify(customerRepository, never()).save(any(Customer.class));
  }

  @Test
  @DisplayName("update: should throw UnprocessableEntityException when all fields are null")
  void update_shouldThrow_whenAllFieldsNull() {
    CustomerUpdateRequest emptyRequest = new CustomerUpdateRequest();

    when(customerRepository.findById(existingId)).thenReturn(Optional.of(customer));

    assertThatThrownBy(() -> customerService.update(existingId, emptyRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessage("At least one field is required.");
  }

  @Test
  @DisplayName("update: should throw UnprocessableEntityException when birthDate is in the future")
  void update_shouldThrow_whenBirthDateInFuture() {
    CustomerUpdateRequest request =
        new CustomerUpdateRequest(null, null, LocalDate.now().plusDays(1), null, null);

    when(customerRepository.findById(existingId)).thenReturn(Optional.of(customer));

    assertThatThrownBy(() -> customerService.update(existingId, request))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessage("birthDate cannot be in the future.");
  }

  @Test
  @DisplayName(
      "update: should throw UnprocessableEntityException when lastName contains invalid characters")
  void update_shouldThrow_whenLastNameInvalid() {
    CustomerUpdateRequest request = new CustomerUpdateRequest("Dupont123", null, null, null, null);

    when(customerRepository.findById(existingId)).thenReturn(Optional.of(customer));

    assertThatThrownBy(() -> customerService.update(existingId, request))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("forbidden characters");
  }

  @Test
  @DisplayName("update: should throw UnprocessableEntityException when email has invalid format")
  void update_shouldThrow_whenEmailInvalid() {
    CustomerUpdateRequest request =
        new CustomerUpdateRequest(null, null, null, "not-an-email", null);

    when(customerRepository.findById(existingId)).thenReturn(Optional.of(customer));

    assertThatThrownBy(() -> customerService.update(existingId, request))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("Invalid email format");
  }
}
