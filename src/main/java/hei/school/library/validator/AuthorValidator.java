package hei.school.library.validator;

import hei.school.library.dto.AuthorRequest;
import org.springframework.stereotype.Component;

@Component
public class AuthorValidator {
  public void validate(AuthorRequest authorRequest) {
    if (authorRequest.getFirstName() == null || authorRequest.getFirstName().isBlank()) {
      throw new UnsupportedOperationException("First name is required");
    }
    if (authorRequest.getLastName() == null || authorRequest.getLastName().isBlank()) {
      throw new UnsupportedOperationException("Last name is required");
    }
    if (authorRequest.getFirstName().length() > 100) {
      throw new UnsupportedOperationException("First name is longer than 100 characters");
    }
    if (authorRequest.getLastName().length() > 100) {
      throw new UnsupportedOperationException("Last name is longer than 100 characters");
    }
  }
}
