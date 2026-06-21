package hei.school.library.service;

import hei.school.library.dto.GenreRequest;
import hei.school.library.dto.GenreResponse;
import hei.school.library.exception.ConflictException;
import hei.school.library.mapper.GenreMapper;
import hei.school.library.repository.dao.GenreRepository;
import hei.school.library.validator.DataValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class GenreService {
  private final GenreRepository genreRepository;
  private final GenreMapper genreMapper;
  private final DataValidator dataValidator;

  @Transactional
  public GenreResponse createGenreByName(GenreRequest request) {
    dataValidator.validateName("name", request.getName());

    return genreMapper.toResponse(
        genreRepository
            .insertGenreIgnoreConflict(request.getName())
            .orElseThrow(
                () -> new ConflictException("Genre " + request.getName() + " already exists")));
  }
}
