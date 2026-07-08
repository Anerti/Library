package hei.school.library.validator;

import hei.school.library.exception.UnprocessableEntityException;
import java.time.Instant;
import javax.swing.*;
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

  public void validateDate(Instant start, Instant end) {
    if (start != null && end != null && end.isBefore(start)) {
      throw new UnprocessableEntityException("Start date must be before end date");
    }
  }

  public void validateSortOrder(String sortOrder) {
    if (sortOrder != null
        && !sortOrder.equalsIgnoreCase("DESC")
        && !sortOrder.equalsIgnoreCase("ASC")) {
      throw new UnprocessableEntityException("Invalid sort order");
    }
  }
}
