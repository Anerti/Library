package hei.school.library.service.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
  private Instant now;

  @BeforeEach
  void setUp() {
    customerService =
        new CustomerService(
            customerRepository, new CustomerMapper(new PaginationMapper()), new DataValidator());

    existingId = UUID.randomUUID();
    now = Instant.now();
  }

  private static Customer customer(
      UUID id,
      String lastName,
      String firstName,
      LocalDate birthDate,
      String email,
      String phone,
      Instant now) {
    return new Customer(id, lastName, firstName, birthDate, email, phone, now, now);
  }

  @Test
  @DisplayName("update: should update firstName only and return DTO")
  void update_shouldUpdateFirstNameOnly() {
    CustomerUpdateRequest request =
        new CustomerUpdateRequest(null, "Marie Claire", null, null, null);

    Customer updated =
        customer(
            existingId,
            "Dupont",
            "Marie Claire",
            LocalDate.of(1995, 3, 10),
            "marie@mail.com",
            "+261****4567",
            now);
    when(customerRepository.patch(
            eq(existingId), isNull(), eq("Marie Claire"), isNull(), isNull(), isNull()))
        .thenReturn(Optional.of(updated));

    CustomerResponse result = customerService.update(existingId, request);

    assertThat(result.getFirstName()).isEqualTo("Marie Claire");
    assertThat(result.getLastName()).isEqualTo("Dupont");
    assertThat(result.getEmail()).isEqualTo("marie@mail.com");
    verify(customerRepository)
        .patch(eq(existingId), isNull(), eq("Marie Claire"), isNull(), isNull(), isNull());
  }

  @Test
  @DisplayName("update: should update lastName only and return DTO")
  void update_shouldUpdateLastNameOnly() {
    CustomerUpdateRequest request = new CustomerUpdateRequest("Martin", null, null, null, null);

    Customer updated =
        customer(
            existingId,
            "Martin",
            "Marie",
            LocalDate.of(1995, 3, 10),
            "marie@mail.com",
            "+261****4567",
            now);
    when(customerRepository.patch(
            eq(existingId), eq("Martin"), isNull(), isNull(), isNull(), isNull()))
        .thenReturn(Optional.of(updated));

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

    Customer updated =
        customer(
            existingId,
            "Dupont",
            "Marie",
            LocalDate.of(1995, 3, 10),
            "new@mail.com",
            "+261****4567",
            now);
    when(customerRepository.patch(
            eq(existingId), isNull(), isNull(), isNull(), eq("new@mail.com"), isNull()))
        .thenReturn(Optional.of(updated));

    CustomerResponse result = customerService.update(existingId, request);

    assertThat(result.getEmail()).isEqualTo("new@mail.com");
    assertThat(result.getLastName()).isEqualTo("Dupont");
  }

  @Test
  @DisplayName("update: should update phone only and return DTO")
  void update_shouldUpdatePhoneOnly() {
    CustomerUpdateRequest request =
        new CustomerUpdateRequest(null, null, null, null, "+261****4000");

    Customer updated =
        customer(
            existingId,
            "Dupont",
            "Marie",
            LocalDate.of(1995, 3, 10),
            "marie@mail.com",
            "+261****4000",
            now);
    when(customerRepository.patch(
            eq(existingId), isNull(), isNull(), isNull(), isNull(), eq("+261****4000")))
        .thenReturn(Optional.of(updated));

    CustomerResponse result = customerService.update(existingId, request);

    assertThat(result.getPhone()).isEqualTo("+261****4000");
    assertThat(result.getLastName()).isEqualTo("Dupont");
  }

  @Test
  @DisplayName("update: should update birthDate only and return DTO")
  void update_shouldUpdateBirthDateOnly() {
    LocalDate newBirthDate = LocalDate.of(1990, 7, 15);
    CustomerUpdateRequest request = new CustomerUpdateRequest(null, null, newBirthDate, null, null);

    Customer updated =
        customer(
            existingId, "Dupont", "Marie", newBirthDate, "marie@mail.com", "+261****4567", now);
    when(customerRepository.patch(
            eq(existingId), isNull(), isNull(), eq(newBirthDate), isNull(), isNull()))
        .thenReturn(Optional.of(updated));

    CustomerResponse result = customerService.update(existingId, request);

    assertThat(result.getBirthDate()).isEqualTo(newBirthDate);
  }

  @Test
  @DisplayName("update: should update all fields at once")
  void update_shouldUpdateAllFields() {
    CustomerUpdateRequest request =
        new CustomerUpdateRequest(
            "Martin", "Jean", LocalDate.of(1988, 1, 1), "jean@mail.com", "+261****4999");

    Customer updated =
        customer(
            existingId,
            "Martin",
            "Jean",
            LocalDate.of(1988, 1, 1),
            "jean@mail.com",
            "+261****4999",
            now);
    when(customerRepository.patch(
            eq(existingId),
            eq("Martin"),
            eq("Jean"),
            eq(LocalDate.of(1988, 1, 1)),
            eq("jean@mail.com"),
            eq("+261****4999")))
        .thenReturn(Optional.of(updated));

    CustomerResponse result = customerService.update(existingId, request);

    assertThat(result.getLastName()).isEqualTo("Martin");
    assertThat(result.getFirstName()).isEqualTo("Jean");
    assertThat(result.getBirthDate()).isEqualTo(LocalDate.of(1988, 1, 1));
    assertThat(result.getEmail()).isEqualTo("jean@mail.com");
    assertThat(result.getPhone()).isEqualTo("+261****4999");
  }

  @Test
  @DisplayName("update: should throw NotFoundException when customer not found")
  void update_shouldThrow_whenNotFound() {
    UUID unknownId = UUID.randomUUID();
    when(customerRepository.patch(
            eq(unknownId), eq("test"), isNull(), isNull(), isNull(), isNull()))
        .thenReturn(Optional.empty());

    assertThatThrownBy(
            () ->
                customerService.update(
                    unknownId, new CustomerUpdateRequest("test", null, null, null, null)))
        .isInstanceOf(NotFoundException.class);

    verify(customerRepository)
        .patch(eq(unknownId), eq("test"), isNull(), isNull(), isNull(), isNull());
  }

  @Test
  @DisplayName("update: should throw UnprocessableEntityException when all fields are null")
  void update_shouldThrow_whenAllFieldsNull() {
    CustomerUpdateRequest emptyRequest = new CustomerUpdateRequest();

    assertThatThrownBy(() -> customerService.update(existingId, emptyRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessage("At least one field is required.");
  }

  @Test
  @DisplayName("update: should throw UnprocessableEntityException when birthDate is in the future")
  void update_shouldThrow_whenBirthDateInFuture() {
    CustomerUpdateRequest request =
        new CustomerUpdateRequest(null, null, LocalDate.now().plusDays(1), null, null);

    assertThatThrownBy(() -> customerService.update(existingId, request))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessage("birthDate cannot be in the future.");
  }

  @Test
  @DisplayName(
      "update: should throw UnprocessableEntityException when lastName contains invalid characters")
  void update_shouldThrow_whenLastNameInvalid() {
    CustomerUpdateRequest request = new CustomerUpdateRequest("Dupont123", null, null, null, null);

    assertThatThrownBy(() -> customerService.update(existingId, request))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("forbidden characters");
  }

  @Test
  @DisplayName("update: should throw UnprocessableEntityException when email has invalid format")
  void update_shouldThrow_whenEmailInvalid() {
    CustomerUpdateRequest request =
        new CustomerUpdateRequest(null, null, null, "not-an-email", null);

    assertThatThrownBy(() -> customerService.update(existingId, request))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("Invalid email format");
  }

  @Test
  @DisplayName("update: should throw UnprocessableEntityException when phone has invalid format")
  void update_shouldThrow_whenPhoneInvalid() {
    CustomerUpdateRequest request =
        new CustomerUpdateRequest(null, null, null, null, "not-a-phone");

    assertThatThrownBy(() -> customerService.update(existingId, request))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("Invalid phone format");
  }
}
