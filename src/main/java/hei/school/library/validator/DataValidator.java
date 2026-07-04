package hei.school.library.validator;

import hei.school.library.dto.UserUpdateRequest;
import hei.school.library.exception.UnprocessableEntityException;
import java.time.LocalDate;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class DataValidator {

  private static final Pattern SAFE_STRING = Pattern.compile("^[a-zA-Z0-9@'éèê ._+\\-]*$");
  private final Pattern SAFE_STRING_BOOK_NAME = Pattern.compile("^[a-zA-Z0-9' éèê-]+$");
  private static final Pattern SAFE_TEXT_STRING = Pattern.compile("^[a-zA-Z0-9' .,;\"!?:éêèç-]+$");
  private static final Pattern SAFE_NAME_STRING = Pattern.compile("^[a-zA-Zéèê' -]+$");
  private static final Pattern VALID_EMAIL_PATTERN =
      Pattern.compile("^[a-zA-Z0-9_.-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z]+){1,2}$");
  private static final Pattern ALLOWED_EMAIL_CHAR = Pattern.compile("^[a-zA-Z0-9.@_-]+$");
  private static final Pattern SAFE_ISBN = Pattern.compile("^[0-9Xx-]{10,}$");
  private static final Pattern VALID_PHONE_PATTERN = Pattern.compile("^[0-9 +]{7,30}$");

  public void checkNull(String fieldName, Object value) {
    if (value == null || value.toString().isBlank()) {
      throw new UnprocessableEntityException(
          String.format("%s is required and cannot be blank.", fieldName));
    }
  }

  public void checkStringLength(String fieldName, String value, int length) {
    if (value != null && value.length() > length) {
      throw new UnprocessableEntityException(
          String.format("%s cannot be longer than %s characters.", fieldName, length));
    }
  }

  public void validateString(String fieldName, String value) {
    if (value != null && !value.isBlank() && !SAFE_STRING.matcher(value).matches()) {
      throw new UnprocessableEntityException(
          String.format(
              "Field '%s' contains invalid characters. Only letters (a-z, A-Z), digits (0-9),"
                  + " spaces, and @ ('.-_) are allowed.",
              fieldName));
    }
  }

  protected void validateBookTitle(String value) {
    if (value != null && !value.isBlank() && !SAFE_STRING_BOOK_NAME.matcher(value).matches()) {
      throw new UnprocessableEntityException("title contains invalid characters.");
    }
  }

  protected void validateText(String fieldName, String value) {
    if (value != null && !value.isBlank() && !SAFE_TEXT_STRING.matcher(value).matches()) {
      throw new UnprocessableEntityException(
          String.format(
              "Field '%s' contains invalid characters. Only a-zA-Z0-9' .,;\"!?:éêè- characters are"
                  + " allowed.",
              fieldName));
    }
  }

  public void validateEmail(String email) {
    if (email != null && !email.isBlank()) {
      checkStringLength("email", email, 100);

      if (!ALLOWED_EMAIL_CHAR.matcher(email).matches()) {
        throw new UnprocessableEntityException(
            String.format(
                "Invalid input for email: '%s' only a-zA-Z0-9@_.- characters are allowed.", email));
      }

      if (!VALID_EMAIL_PATTERN.matcher(email).matches()) {
        throw new UnprocessableEntityException(String.format("Invalid email format: '%s'", email));
      }
    }
  }

  public void validatePhone(String phone) {
    if (phone != null && !phone.isBlank()) {
      checkStringLength("phone", phone, 30);

      if (!VALID_PHONE_PATTERN.matcher(phone).matches()) {
        throw new UnprocessableEntityException(
            String.format(
                "Invalid phone format: '%s'. Only +, digits, spaces, hyphens and parentheses are"
                    + " allowed.",
                phone));
      }
    }
  }

  public void checkPasswordSecurityLevel(String password) {
    checkNull("password", password);

    if (password.length() < 12) {
      throw new UnprocessableEntityException("password must be at least 12 characters.");
    }

    if (!password.matches(".*[A-Z].*")) {
      throw new UnprocessableEntityException(
          "Password must contain at least one uppercase character.");
    }

    if (!password.matches(".*[a-z].*")) {
      throw new UnprocessableEntityException(
          "Password must contain at least one lowercase character.");
    }

    if (!password.matches(".*[0-9].*")) {
      throw new UnprocessableEntityException("Password must contain at least one digits.");
    }

    if (!password.matches(".*[!?*+=@#$%^&()_\\-\\[\\]{}|\\\\:;\"'<>,./`~].*")) {
      throw new UnprocessableEntityException(
          "Password must contain at least one special character.");
    }
  }

  public void validateIsbn(String isbn) {
    if (isbn == null || isbn.isBlank()) {
      throw new UnprocessableEntityException("isbn is required.");
    }
    if (isbn.length() > 100) {
      throw new UnprocessableEntityException("isbn cannot be longer than 100 characters.");
    }
    if (!SAFE_ISBN.matcher(isbn).matches()) {
      throw new UnprocessableEntityException("isbn is invalid or contain Illegal characters.");
    }
  }

  public void validateName(String fieldName, String value) {
    if (value != null && !value.isBlank()) {
      checkStringLength(fieldName, value, 100);

      if (!SAFE_NAME_STRING.matcher(value).matches()) {
        throw new UnprocessableEntityException(
            String.format(
                "%s field contain forbidden characters. "
                    + "Only letters (a-z, A-Z, éèê), hyphen and space are allowed.",
                fieldName));
      }
    }
  }

}
