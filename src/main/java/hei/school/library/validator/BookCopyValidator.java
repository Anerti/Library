package hei.school.library.validator;

import hei.school.library.dto.BookCopyRequest;
import hei.school.library.dto.BookCopyUpdateRequest;
import hei.school.library.exception.BadRequestException;
import org.springframework.stereotype.Component;

@Component
public class BookCopyValidator {

  public void validateCreate(BookCopyRequest request) {
    if (request.getBookId() == null) {
      throw new BadRequestException("bookId is required");
    }
    if (request.getPrice() == null || request.getPrice() <= 0) {
      throw new BadRequestException("price must be greater than 0");
    }
    if (request.getFormat() == null) {
      throw new BadRequestException("format is required");
    }
    if (request.getPageNumber() == null || request.getPageNumber() <= 0) {
      throw new BadRequestException("pageNumber must be greater than 0");
    }
  }

  public void validateUpdate(BookCopyUpdateRequest request) {
    if (request.getPrice() == null
        && request.getFormat() == null
        && request.getStatus() == null
        && request.getPageNumber() == null) {
      throw new BadRequestException("At least one field is required");
    }
    if (request.getPrice() != null && request.getPrice() <= 0) {
      throw new BadRequestException("price must be greater than 0");
    }
    if (request.getPageNumber() != null && request.getPageNumber() <= 0) {
      throw new BadRequestException("pageNumber must be greater than 0");
    }
  }
}
