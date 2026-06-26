package hei.school.library.service;

import hei.school.library.dto.GenreListResponse;
import hei.school.library.dto.GenreRequest;
import hei.school.library.dto.GenreResponse;
import hei.school.library.exception.ConflictException;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.GenreMapper;
import hei.school.library.repository.dao.GenreRepository;
import hei.school.library.validator.DataValidator;
import java.sql.SQLException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class GenreService {
  private final GenreRepository genreRepository;
  private final GenreMapper genreMapper;
  private final DataValidator dataValidator;
  private static final String SQL_STATE_VIOLATION = "23505";

  @Transactional(readOnly = true)
  public GenreResponse getGenreById(UUID id) {
    return genreMapper.toResponse(
        genreRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException(String.format("Genre %s not found", id))));
  }

  @Transactional
  public GenreResponse createGenreByName(GenreRequest request) {
    dataValidator.checkNull("name", request.getName());
    dataValidator.validateName("name", request.getName());

    return genreMapper.toResponse(
        genreRepository
            .insertGenreIgnoreConflict(request.getName())
            .orElseThrow(
                () ->
                    new ConflictException(
                        String.format("Genre %s already exists.", request.getName()))));
  }

  @Transactional(readOnly = true)
  public GenreListResponse findAll(String search, int page, int size) {
    PageRequest pageable = PageRequest.of(page - 1, size);

    return (search == null || search.isBlank())
        ? genreMapper.toPageResponse(genreRepository.findAll(pageable), page, size)
        : genreMapper.toPageResponse(genreRepository.findBySearch(search, pageable), page, size);
  }

  @Transactional
  public void deleteGenreById(UUID id) {
    genreRepository
        .deleteByUUId(id)
        .orElseThrow(() -> new NotFoundException(String.format("Genre %s not found", id)));
  }

  @Transactional
  public GenreResponse updateGenreByName(UUID id, GenreRequest request) {
    dataValidator.checkNull("name", request.getName());
    dataValidator.validateName("name", request.getName());

    try {
      return genreMapper.toResponse(
          genreRepository
              .updateGenreName(id, request.getName())
              .orElseThrow(() -> new NotFoundException("Genre with id " + id + " not found")));
    } catch (DataIntegrityViolationException e) {
      if (e.getRootCause() instanceof SQLException sqlEx
          && SQL_STATE_VIOLATION.equals(sqlEx.getSQLState())) {
        throw new ConflictException("Genre " + request.getName() + " already exists.");
      }
      throw e;
    }
  }
}
