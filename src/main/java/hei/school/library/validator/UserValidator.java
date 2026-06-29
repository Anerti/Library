package hei.school.library.validator;

import hei.school.library.dto.UserRequest;
import hei.school.library.exception.UnprocessableEntityException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final DataValidator dataValidator;

    public void validateUserCreation(UserRequest request) {
        dataValidator.checkNull("lastName", request.getLastName());
        dataValidator.validateName("lastName", request.getLastName());

        dataValidator.checkNull("firstName", request.getFirstName());
        dataValidator.validateName("firstName", request.getFirstName());

        dataValidator.checkNull("email", request.getEmail());
        dataValidator.validateEmail(request.getEmail());

        dataValidator.checkPasswordSecurityLevel(request.getPassword());

        dataValidator.checkNull("confirmPassword", request.getConfirmPassword());

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new UnprocessableEntityException("Passwords do not match.");
        }

        dataValidator.validatePhone(request.getPhone());

        dataValidator.checkNull("birthDate", request.getBirthDate());

        if (request.getBirthDate().isAfter(LocalDate.now())) {
            throw new UnprocessableEntityException("BirthDate cannot be in the future.");
        }

        if (request.getBirthDate().plusYears(12).isAfter(LocalDate.now())) {
            throw new UnprocessableEntityException("You must be at least 12 years old to create an account.");
        }
    }
}
