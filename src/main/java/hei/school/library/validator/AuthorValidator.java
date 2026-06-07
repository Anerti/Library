package hei.school.library.validator;

import hei.school.library.dto.AuthorRequest;
import hei.school.library.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class AuthorValidator {
  public void validate(AuthorRequest authorRequest) {
    if (authorRequest.getFirstName() == null || authorRequest.getFirstName().isBlank()) {
      throw new ValidationException("First name is required");
    }
    if (authorRequest.getLastName() == null || authorRequest.getLastName().isBlank()) {
      throw new ValidationException("Last name is required");
    }
    if (authorRequest.getFirstName().length() > 100) {
      throw new ValidationException("First name is longer than 100 characters");
    }
    if (authorRequest.getLastName().length() > 100) {
      throw new ValidationException("Last name is longer than 100 characters");
    }
  }
}
