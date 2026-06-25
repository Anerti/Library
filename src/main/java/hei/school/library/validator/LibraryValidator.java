package hei.school.library.validator;

import hei.school.library.dto.LibraryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LibraryValidator {
  private final DataValidator dataValidator;

  public void validateCreation(LibraryRequest request) {
    dataValidator.checkNull("name", request.getName());
    dataValidator.validateName("name", request.getName());

    dataValidator.checkNull("phone", request.getPhone());
    dataValidator.validatePhone(request.getPhone());

    dataValidator.validateEmail(request.getEmail());

    dataValidator.checkNull("address", request.getAddress());
    dataValidator.checkStringLength("address", request.getAddress(), 100);
    dataValidator.validateString("address", request.getAddress());
  }
}
