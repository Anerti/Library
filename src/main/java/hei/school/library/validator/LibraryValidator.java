package hei.school.library.validator;

import hei.school.library.dto.LibraryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LibraryValidator {
    private final DataValidator dataValidator;

    public void validateCreation(LibraryRequest request) {
        dataValidator.validateName("name", request.getName());
        dataValidator.validatePhone(request.getPhone());
        dataValidator.validateEmail(request.getEmail());
        dataValidator.validateName("address", request.getAddress());
    }
}
