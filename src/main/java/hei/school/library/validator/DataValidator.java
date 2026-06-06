package hei.school.library.validator;

import hei.school.library.exception.UnprocessableEntityException;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class DataValidator {

  private static final Pattern SAFE_STRING = Pattern.compile("^[a-zA-Z0-9@' ._-]*$");

  public void validateString(String fieldName, String value) {
    if (value != null && !value.isBlank() && !SAFE_STRING.matcher(value).matches()) {
      throw new UnprocessableEntityException(
          String.format(
              "Field '%s' contains invalid characters. Only letters (a-z, A-Z), digits (0-9), and @ ' . - _ are allowed.",
              fieldName));
    }
  }
}
