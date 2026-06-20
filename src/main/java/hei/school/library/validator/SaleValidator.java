package hei.school.library.validator;

import hei.school.library.dto.SaleRequest;
import hei.school.library.exception.UnprocessableEntityException;
import org.springframework.stereotype.Component;

@Component
public class SaleValidator {

  public void validateCreate(SaleRequest request) {
    if (request.getCustomer() == null) {
      throw new UnprocessableEntityException("customer is required.");
    }
    if (request.getCustomer().getLastName() == null
        || request.getCustomer().getLastName().isBlank()) {
      throw new UnprocessableEntityException("customer.lastName is required.");
    }
    if (request.getCustomer().getEmail() == null || request.getCustomer().getEmail().isBlank()) {
      throw new UnprocessableEntityException("customer.email is required.");
    }
    if (request.getCustomer().getBirthDate() == null) {
      throw new UnprocessableEntityException("customer.birthDate is required.");
    }
    if (request.getCustomer().getBirthDate().isAfter(java.time.LocalDate.now())) {
      throw new UnprocessableEntityException("customer.birthDate cannot be in the future.");
    }
  }
}
