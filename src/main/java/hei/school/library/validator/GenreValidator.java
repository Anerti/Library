package hei.school.library.validator;

import hei.school.library.exception.BadRequestException;
import hei.school.library.repository.GenreRepository;
import org.springframework.stereotype.Component;

@Component
public class GenreValidator {
    final private GenreRepository genreRepository;
    public GenreValidator(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }
    public void isExistByName(String name) {
        if (genreRepository.existsByNameIgnoreCase(name) ){
            throw new BadRequestException("Genre already exists");
        }
    }

}
