package hei.school.library.validator;

import hei.school.library.dto.AuthorRequest;
import hei.school.library.dto.AuthorUpdateRequest;
import hei.school.library.exception.UnprocessableEntityException;
import org.springframework.stereotype.Component;

@Component
public class AuthorValidator {
  public void validateCreate(AuthorRequest request) {
    if (request.getFirstName() != null && request.getFirstName().length() > 100) {
      throw new UnprocessableEntityException("First name is longer than 100 characters");
    }
    if (request.getLastName() == null || request.getLastName().isBlank()) {
      throw new UnprocessableEntityException("Last name is required");
    }
    if (request.getLastName().length() > 100) {
      throw new UnprocessableEntityException("Last name is longer than 100 characters");
    }
  }

  public void validateUpdate(AuthorUpdateRequest request) {
    if (request.getFirstName() == null && request.getLastName() == null) {
      throw new UnprocessableEntityException("Minimum first name or last name is required");
    }
    if (request.getFirstName() != null && request.getFirstName().length() > 100) {
      throw new UnprocessableEntityException("First name is longer than 100 characters");
    }
    if (request.getLastName() != null && request.getLastName().length() > 100) {
      throw new UnprocessableEntityException("Last name is longer than 100 characters");
    }
  }
}
