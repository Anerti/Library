package hei.school.library.validator;

import hei.school.library.exception.UnprocessableEntityException;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsValidator {

  public void validateFormat(String format) {
    if (format != null
        && !format.equals("ALL")
        && !format.equals("PAPERBACK")
        && !format.equals("POCKET")
        && !format.equals("HARDCOVER")) {
      throw new UnprocessableEntityException("Invalid format");
    }
  }

  public void validateThreshold(int threshold) {
    if (threshold < 0) {
      throw new UnprocessableEntityException("Threshold must be greater than 0");
    }
  }
}
