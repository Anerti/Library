package hei.school.library.validator;

import hei.school.library.exception.UnprocessableEntityException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnalyticsValidator {
  private final DataValidator dataValidator;

  public void validateFormat(String format) {
    if (format == null
        || !format.equals("ALL")
            && !format.equals("PAPERBACK")
            && !format.equals("POCKET")
            && !format.equals("HARDCOVER")) {
      throw new UnprocessableEntityException("Invalid format");
    }
  }

  public void validateFilters(int threshold, String genre, String author, String title, String isbn) {
    if (threshold < 0) {
      throw new UnprocessableEntityException("Threshold must be greater than 0");
    }

    dataValidator.validateName("genre", genre);
    dataValidator.validateName("author", author);
    dataValidator.validateBookTitle(title);

    if (isbn != null && !isbn.isBlank()) {
      dataValidator.validateIsbn(isbn);
    }
  }
}
