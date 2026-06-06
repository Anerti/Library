package hei.school.library.validator;

import hei.school.library.dto.genre.GenreRequest;
import hei.school.library.exception.BadRequestException;
import hei.school.library.repository.GenreRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GenreValidator {
    final private GenreRepository genreRepository;
    public GenreValidator(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }
    public void isExistByName(String name) {
        if (genreRepository.existsByNameIgnoreCase(name) ){
            throw new BadRequestException("The requested resource already exists");
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
    public void isIdValid(UUID id) {
        if (id == null) {
            throw new BadRequestException("The request body contains invalid JSON or a parameter is malformed");
        }
    }

}
