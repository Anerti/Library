package hei.school.library.service;

import hei.school.library.dto.GenreResponse;
import hei.school.library.entity.Genre;
import hei.school.library.exception.NotFoundException;
import hei.school.library.repository.dao.GenreRepository;
import hei.school.library.validator.DataValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import hei.school.library.mapper.GenreMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class GenreService {
    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;
    private final DataValidator dataValidator;

    @Transactional(readOnly = true)
    public GenreResponse getGenreById(UUID id) {
        dataValidator.validateString("id", id == null ? null : id.toString());
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() ->new NotFoundException("The requested resource was not found"));
        return genreMapper.toResponse(genre);
    }

}