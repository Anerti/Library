package hei.school.library.service;

import hei.school.library.dto.GenreRequest;
import hei.school.library.dto.GenreResponse;
import hei.school.library.dto.PageResponse;
import hei.school.library.entity.Genre;
import hei.school.library.exception.ConflictException;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.GenreMapper;
import hei.school.library.repository.dao.GenreRepository;
import hei.school.library.validator.DataValidator;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class GenreService {
  private final GenreRepository genreRepository;
  private final GenreMapper genreMapper;
  private final DataValidator dataValidator;

  @Transactional(readOnly = true)
  public GenreResponse getGenreById(UUID id) {
    Genre genre =
        genreRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("The requested resource was not found"));
    return genreMapper.toResponse(genre);
  }

  @Transactional
  public GenreResponse createGenreByName(GenreRequest request) {
    dataValidator.validateName("name", request.getName());

    return genreMapper.toResponse(
        genreRepository
            .insertGenreIgnoreConflict(request.getName())
            .orElseThrow(
                () -> new ConflictException("Genre " + request.getName() + " already exists")));
  }
    @Transactional(readOnly = true)
    public PageResponse<GenreResponse> findAll(String search, int page, int size) {
        PageRequest pageable = PageRequest.of(page - 1, size);

        return (search == null || search.isBlank())
                ? genreMapper.toPageResponse(genreRepository.findAll(pageable), page, size)
                : genreMapper.toPageResponse(genreRepository.findBySearch(search, pageable), page, size);
    }

  @Transactional
  public void deleteGenreById(UUID id) {
    genreRepository
        .deleteByUUId(id)
        .orElseThrow(() -> new NotFoundException("Genre with id " + id + " not found"));
  }

  @Transactional
  public GenreResponse updateGenreByName(UUID id, GenreRequest request) {
    dataValidator.validateName("name", request.getName());
    return genreMapper.toResponse(
        genreRepository
            .updateGenreName(id, request.getName())
            .orElseThrow(() -> new NotFoundException("Genre with id " + id + " not found")));
  }
}
