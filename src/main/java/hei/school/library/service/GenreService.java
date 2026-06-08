package hei.school.library.service;

import hei.school.library.dto.GenreRequest;
import hei.school.library.dto.GenreResponse;
import hei.school.library.entity.Genre;
import hei.school.library.repository.GenreRepository;
import hei.school.library.validator.DataValidator;
import hei.school.library.validator.GenreValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import hei.school.library.mapper.GenreConverter;
import org.springframework.transaction.annotation.Transactional;
@RequiredArgsConstructor
@Service
public class GenreService {
    private final GenreRepository genreRepository;
    private final GenreConverter genreConverter;
    private final GenreValidator genreValidator;
    private final DataValidator dataValidator;

    @Transactional
    public GenreResponse createGenreByName(GenreRequest request) {
        dataValidator.validateString("name", request.getName());
        dataValidator.validateName("name", request.getName());
        genreValidator.isExistByName(genreRepository.existsByNameIgnoreCase(request.getName()));
        Genre genre = Genre.builder().name(request.getName()).build();

        return genreConverter.toResponse(
                genreRepository.save(genre)
        );
    }

}
