package hei.school.library.service.customer;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import hei.school.library.dto.CustomerResponse;
import hei.school.library.entity.Customer;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.CustomerMapper;
import hei.school.library.mapper.PaginationMapper;
import hei.school.library.repository.dao.CustomerRepository;
import hei.school.library.service.CustomerService;
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
class GetCustomersByIdServiceTest {

  @Mock private CustomerRepository customerRepository;

  private CustomerService customerService;

  private UUID existingId;
  private UUID unknownId;
  private Customer customer;

  @BeforeEach
  void setUp() {
    customerService =
        new CustomerService(
            customerRepository, new CustomerMapper(new PaginationMapper()), new DataValidator());

    existingId = UUID.randomUUID();
    unknownId = UUID.randomUUID();
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
  @DisplayName("findById: should return customer when found")
  void findById_shouldReturnCustomer() {
    when(customerRepository.findById(existingId)).thenReturn(Optional.of(customer));

    CustomerResponse result = customerService.findById(existingId);

    assertThat(result.getLastName()).isEqualTo("Dupont");
    assertThat(result.getEmail()).isEqualTo("marie@mail.com");
    verify(customerRepository).findById(existingId);
  }

  @Test
  @DisplayName("findById: should throw NotFoundException when absent")
  void findById_shouldThrow_whenNotFound() {
    when(customerRepository.findById(unknownId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> customerService.findById(unknownId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(unknownId.toString());
  }
}
