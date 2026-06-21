package hei.school.library.service.customer;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.CustomerMapper;
import hei.school.library.mapper.PaginationMapper;
import hei.school.library.repository.dao.CustomerRepository;
import hei.school.library.service.CustomerService;
import hei.school.library.validator.DataValidator;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteCustomersByIdServiceTest {

  @Mock private CustomerRepository customerRepository;

  private CustomerService customerService;

  private UUID existingId;
  private UUID unknownId;

  @BeforeEach
  void setUp() {
    customerService =
        new CustomerService(
            customerRepository, new CustomerMapper(new PaginationMapper()), new DataValidator());

    existingId = UUID.randomUUID();
    unknownId = UUID.randomUUID();
  }

  @Test
  @DisplayName("delete: should delete when customer exists")
  void delete_shouldDelete_whenExists() {
    when(customerRepository.delete(existingId)).thenReturn(Optional.of(existingId));

    customerService.delete(existingId);

    verify(customerRepository).delete(existingId);
  }

  @Test
  @DisplayName("delete: should throw NotFoundException when absent")
  void delete_shouldThrow_whenNotFound() {
    when(customerRepository.delete(unknownId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> customerService.delete(unknownId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(unknownId.toString());

    verify(customerRepository).delete(unknownId);
  }
}
