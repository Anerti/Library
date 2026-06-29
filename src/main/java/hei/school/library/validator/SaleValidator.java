package hei.school.library.validator;

import hei.school.library.dto.SaleRequest;
import hei.school.library.dto.SaleUpdateRequest;
import hei.school.library.exception.UnprocessableEntityException;
import org.springframework.stereotype.Component;

@Component
public class SaleValidator {

  public void validateCreate(SaleRequest request) {
    if (request.getUser() == null) {
      throw new UnprocessableEntityException("user is required.");
    }
    if (request.getUser().getLastName() == null || request.getUser().getLastName().isBlank()) {
      throw new UnprocessableEntityException("user.lastName is required.");
    }
    if (request.getUser().getEmail() == null || request.getUser().getEmail().isBlank()) {
      throw new UnprocessableEntityException("user.email is required.");
    }
    if (request.getUser().getBirthDate() == null) {
      throw new UnprocessableEntityException("user.birthDate is required.");
    }
    if (request.getUser().getBirthDate().isAfter(java.time.LocalDate.now())) {
      throw new UnprocessableEntityException("user.birthDate cannot be in the future.");
    }
  }

  public void validateUpdate(SaleUpdateRequest request) {
    if (request.getStatus() == null && request.getSaleDate() == null) {
      throw new UnprocessableEntityException("At least one field is required.");
    }
  }
}
