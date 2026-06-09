package hei.school.library.validator;

import hei.school.library.dto.CustomerRequest;
import hei.school.library.dto.CustomerUpdateRequest;
import hei.school.library.exception.UnprocessableEntityException;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class CustomerValidator {

  public void validateCreate(CustomerRequest request) {
    if (request.getLastName() == null || request.getLastName().isBlank()) {
      throw new UnprocessableEntityException("lastName is required.");
    }
    if (request.getLastName().length() > 100) {
      throw new UnprocessableEntityException("lastName cannot be longer than 100 characters.");
    }
    if (request.getFirstName() != null && request.getFirstName().length() > 100) {
      throw new UnprocessableEntityException("firstName cannot be longer than 100 characters.");
    }
    if (request.getEmail() == null || request.getEmail().isBlank()) {
      throw new UnprocessableEntityException("email is required.");
    }
    if (request.getEmail().length() > 100) {
      throw new UnprocessableEntityException("email cannot be longer than 100 characters.");
    }
    if (request.getBirthDate() == null) {
      throw new UnprocessableEntityException("birthDate is required.");
    }
    if (request.getBirthDate().isAfter(LocalDate.now())) {
      throw new UnprocessableEntityException("birthDate cannot be in the future.");
    }
  }

  public void validateUpdate(CustomerUpdateRequest request) {
    if (request.getLastName() == null
        && request.getFirstName() == null
        && request.getBirthDate() == null
        && request.getEmail() == null
        && request.getPhone() == null) {
      throw new UnprocessableEntityException("At least one field is required.");
    }
    if (request.getLastName() != null && request.getLastName().length() > 100) {
      throw new UnprocessableEntityException("lastName cannot be longer than 100 characters.");
    }
    if (request.getFirstName() != null && request.getFirstName().length() > 100) {
      throw new UnprocessableEntityException("firstName cannot be longer than 100 characters.");
    }
    if (request.getEmail() != null && request.getEmail().length() > 100) {
      throw new UnprocessableEntityException("email cannot be longer than 100 characters.");
    }
    if (request.getBirthDate() != null && request.getBirthDate().isAfter(LocalDate.now())) {
      throw new UnprocessableEntityException("birthDate cannot be in the future.");
    }
  }
}
