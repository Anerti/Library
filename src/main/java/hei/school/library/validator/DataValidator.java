package hei.school.library.validator;

import hei.school.library.dto.CustomerRequest;
import hei.school.library.exception.UnprocessableEntityException;

import java.time.LocalDate;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class DataValidator {

  private static final Pattern SAFE_SEARCH_STRING = Pattern.compile("^[a-zA-Z0-9@' ._-]*$");
  private static final Pattern SAFE_NAME_STRING = Pattern.compile("^[a-zA-Z' ]+$");
  private static final Pattern VALID_EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_.-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z]+){1,2}$");
  private static final Pattern ALLOWED_EMAIL_CHAR = Pattern.compile("^[a-zA-Z0-9.@_-]+$");

  public void validateString(String fieldName, String value) {
    if (value != null && !value.isBlank() && !SAFE_SEARCH_STRING.matcher(value).matches()) {
      throw new UnprocessableEntityException(
          String.format(
              "Field '%s' contains invalid characters. Only letters (a-z, A-Z), digits (0-9), and @"
                  + " ' . - _ are allowed.",
              fieldName));
    }
  }

  public void validateEmail(String email){
    if (email == null && email.isBlank()) {
      throw new UnprocessableEntityException("email is required.");
    }

    if (email.length() > 100){
      throw new UnprocessableEntityException("email cannot be longer than 100 characters.");
    }

    if (!ALLOWED_EMAIL_CHAR.matcher(email).matches()){
      throw new UnprocessableEntityException(String.format("Invalid input for email: '%s' only a-zA-Z0-9@_.- characters are allowed.", email));
    }

    if (!VALID_EMAIL_PATTERN.matcher(email).matches()){
      throw new UnprocessableEntityException(String.format("Invalid email format: '%s'", email));
    }
  }

  public void validateCustomer(CustomerRequest request) {
    validateName("lastName", request.getLastName());
    validateName("firstName", request.getFirstName());
    validateEmail(request.getEmail());

    if (request.getBirthDate() == null) {
      throw new UnprocessableEntityException("birthDate is required.");
    }
    if (request.getBirthDate().isAfter(LocalDate.now())) {
      throw new UnprocessableEntityException("birthDate cannot be in the future.");
    }
  }

  public void validateName(String fieldName, String value) {
    if (value == null || value.isBlank()) {
      throw new UnprocessableEntityException(String.format("%s is required.", fieldName));
    }

    if (value.length() > 100) {
      throw new UnprocessableEntityException(
          String.format("%s cannot be longer than 100 characters.", fieldName));
    }

    if (!SAFE_NAME_STRING.matcher(value).matches()) {
      throw new UnprocessableEntityException(
          String.format(
              "%s field contain forbidden characters. "
                  + "Only letters (a-z, A-Z) and space are allowed.",
              fieldName));
    }
  }
}
