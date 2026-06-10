package hei.school.library.validator;

import hei.school.library.dto.BookUpdateRequest;
import hei.school.library.exception.UnprocessableEntityException;
import org.springframework.stereotype.Component;

@Component
public class BookValidator {

  public void validateUpdate(BookUpdateRequest request) {
    if (request.getTitle() == null
        && request.getSummary() == null
        && request.getIsbn() == null
        && request.getPublisher() == null
        && request.getPublishedAt() == null) {
      throw new UnprocessableEntityException(
          "At least one field (title, summary, isbn, publisher, publishedAt) must be provided");
    }
  }
}
