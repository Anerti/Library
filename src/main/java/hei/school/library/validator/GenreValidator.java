package hei.school.library.validator;

import hei.school.library.dto.GenreRequest;
import hei.school.library.exception.BadRequestException;
import hei.school.library.exception.ConflictException;
import hei.school.library.exception.UnprocessableEntityException;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class GenreValidator {
    private static final Pattern SAFE_STRING = Pattern.compile("^[a-zA-Z0-9@' ._-]*$");

    public void validateString(String fieldName, String value) {
        if (value != null && !value.isBlank() && !SAFE_STRING.matcher(value).matches()) {
            throw new UnprocessableEntityException(
                    String.format(
                            "Field '%s' contains invalid characters. Only letters (a-z, A-Z), digits (0-9), and @"
                                    + " ' . - _ are allowed.",
                            fieldName));
        }
    }
    public void isExistByName(boolean isExist) {
        if (isExist) {
            throw new ConflictException("The requested resource already exists");
        }
    }
    public void isRequestValid(GenreRequest request) {
        if (request == null) {
            throw new BadRequestException("The request body contains invalid JSON or a parameter is malformed");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new BadRequestException("The request body contains invalid JSON or a parameter is malformed");
        }
    }

}
