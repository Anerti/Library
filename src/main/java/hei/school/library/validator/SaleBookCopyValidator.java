package hei.school.library.validator;

import hei.school.library.dto.SaleBookCopyRequest;
import hei.school.library.exception.UnprocessableEntityException;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class SaleBookCopyValidator {

  public void validateCreate(SaleBookCopyRequest request) {
    if (request.getBookCopyId() == null) {
      throw new UnprocessableEntityException("bookCopyId is required.");
    }
    if (request.getPrice() == null) {
      throw new UnprocessableEntityException("price is required.");
    }
    if (request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
      throw new UnprocessableEntityException("price must be greater than 0.");
    }
  }
}
