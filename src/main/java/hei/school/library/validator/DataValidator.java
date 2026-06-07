package hei.school.library.validator;

import hei.school.library.exception.UnprocessableEntityException;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class DataValidator {

  private static final Pattern SAFE_SEARCH_STRING = Pattern.compile("^[a-zA-Z0-9@' ._-]*$");
  private static final Pattern SAFE_NAME_STRING = Pattern.compile("^[a-zA-Z' ]+$");

  public void validateString(String fieldName, String value) {
    if (value != null && !value.isBlank() && !SAFE_SEARCH_STRING.matcher(value).matches()) {
      throw new UnprocessableEntityException(
          String.format(
              "Field '%s' contains invalid characters. Only letters (a-z, A-Z), digits (0-9), and @"
                  + " ' . - _ are allowed.",
              fieldName));
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
