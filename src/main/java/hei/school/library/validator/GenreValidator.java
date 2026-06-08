package hei.school.library.validator;
import hei.school.library.exception.ConflictException;
import org.springframework.stereotype.Component;
@Component
public class GenreValidator {
    public void isExistByName(boolean isExist) {
        if (isExist) {
            throw new ConflictException("The requested resource already exists");
        }
    }
}