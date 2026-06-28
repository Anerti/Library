package hei.school.library.validator;

import hei.school.library.dto.BookRequest;
import hei.school.library.dto.BookUpdateRequest;
import hei.school.library.exception.UnprocessableEntityException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookValidator {
  private final DataValidator dataValidator;

  private void titleValidator(String title) {
    dataValidator.checkNull("title", title);
    dataValidator.checkStringLength("title", title, 100);
    dataValidator.validateBookTitle(title);
  }

  private void summaryValidator(String summary) {
    dataValidator.checkStringLength("summary", summary, 1000);
    dataValidator.validateText("summary", summary);
  }

  private void isbnValidator(String isbn) {
    dataValidator.checkNull("isbn", isbn);
    dataValidator.validateIsbn(isbn);
  }

  private void publisherValidator(String publisher) {
    dataValidator.checkNull("publisher", publisher);
    dataValidator.validateName("publisher", publisher);
  }

  public void validateCreation(BookRequest request) {
    dataValidator.checkNull("title", request.getTitle());
    dataValidator.checkStringLength("title", request.getTitle(), 100);
    dataValidator.validateBookTitle(request.getTitle());

    dataValidator.checkStringLength("summary", request.getSummary(), 1000);
    dataValidator.validateText("summary", request.getSummary());

    dataValidator.checkNull("isbn", request.getIsbn());
    dataValidator.validateIsbn(request.getIsbn());

    dataValidator.checkNull("publisher", request.getPublisher());
    dataValidator.checkStringLength("publisher", request.getPublisher(), 100);
    dataValidator.validateName("publisher", request.getPublisher());

    dataValidator.checkNull("publishedAt", request.getPublishedAt());
  }

  public void validateFetch(String title, String publisher, String isbn, String authorLastName, String genre) {
    dataValidator.validateBookTitle(title);
    dataValidator.validateName("publisher", publisher);

    if (isbn != null && !isbn.isBlank()) {
      dataValidator.validateIsbn(isbn);
    }

    dataValidator.validateName("lastName", authorLastName);
    dataValidator.validateName("genre", genre);
  }

  public void validateUpdate(BookUpdateRequest request) {
    if (request.getTitle() == null
        && request.getSummary() == null
        && request.getIsbn() == null
        && request.getPublisher() == null
        && request.getPublishedAt() == null) {
      throw new UnprocessableEntityException(
          "At least one field (title, summary, isbn, publisher, publishedAt) must be provided");
    }

    if (request.getTitle() != null) {
      titleValidator(request.getTitle());
    }

    if (request.getSummary() != null) {
      summaryValidator(request.getSummary());
    }

    if (request.getIsbn() != null) {
      isbnValidator(request.getIsbn());
    }

    if (request.getPublisher() != null) {
      publisherValidator(request.getPublisher());
    }
  }
}
