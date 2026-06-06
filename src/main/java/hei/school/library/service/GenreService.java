package hei.school.library.service;

import hei.school.library.dto.genre.GenreRequest;
import hei.school.library.dto.genre.GenreResponse;
import hei.school.library.entity.GenreEntity;
import hei.school.library.repository.GenreRepository;
import hei.school.library.validator.GenreValidator;
import org.springframework.stereotype.Service;
import hei.school.library.converter.GenreConverter;
@Service
public class GenreService {
    private final GenreRepository genreRepository;
    private final GenreConverter genreConverter;
    private final GenreValidator genreValidator;

    public GenreService(GenreRepository genreRepository, GenreConverter genreConverter, GenreValidator genreValidator) {
        this.genreRepository = genreRepository;
        this.genreConverter = genreConverter;
        this.genreValidator = genreValidator;
    }
    public GenreResponse createGenreByName(GenreRequest request) {
        genreValidator.isExistByName(request.getName());
        GenreEntity genre = new GenreEntity();
        genre.setName(request.getName());

        return genreConverter.toResponse(
                genreRepository.save(genre)
        );
    }
}
