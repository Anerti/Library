package hei.school.library.service.genre;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import hei.school.library.dto.GenreRequest;
import hei.school.library.dto.GenreResponse;
import hei.school.library.entity.Genre;
import hei.school.library.exception.ConflictException;
import hei.school.library.exception.NotFoundException;
import hei.school.library.exception.UnprocessableEntityException;
import hei.school.library.mapper.GenreMapper;
import hei.school.library.repository.dao.GenreRepository;
import hei.school.library.service.GenreService;
import hei.school.library.validator.DataValidator;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GenreServiceTest {
  @Mock private GenreRepository genreRepository;
  @Mock private DataValidator dataValidator;
  @Mock private GenreMapper genreMapper;

  private GenreService genreService;

  @BeforeEach
  void setUp() {
    genreService = new GenreService(genreRepository, genreMapper, dataValidator);
  }

  @Test
  void should_create_genre() throws Exception {

    UUID id = UUID.randomUUID();
    String genreName = "Fantasy";

    Genre genre =
        Genre.builder()
            .id(id)
            .name(genreName)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    GenreResponse expectedResponse = GenreResponse.builder().id(id).name(genreName).build();

    when(genreRepository.insertGenreIgnoreConflict(genreName)).thenReturn(Optional.of(genre));
    when(genreMapper.toResponse(genre)).thenReturn(expectedResponse);

    GenreResponse actualResponse =
        genreService.createGenreByName(GenreRequest.builder().name(genreName).build());
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void should_return_conflict_when_genre_already_exists() throws Exception {
    String genreName = "Fantasy";
    when(genreRepository.insertGenreIgnoreConflict(genreName)).thenReturn(Optional.empty());
    assertThrows(
        ConflictException.class,
        () -> genreService.createGenreByName(GenreRequest.builder().name(genreName).build()));
  }

  @Test
  void should_return_unprocessable_entity_exception_when_request_is_not_valid() throws Exception {
    doThrow(UnprocessableEntityException.class).when(dataValidator).validateName("name", null);

    assertThrows(
        UnprocessableEntityException.class,
        () -> genreService.createGenreByName(GenreRequest.builder().name(null).build()));
  }

  @Test
  void should_delete_genre_by_id() throws Exception {

    UUID id = UUID.randomUUID();
    when(genreRepository.deleteByUUId(id)).thenReturn(Optional.of(id));
    genreService.deleteGenreById(id);

    verify(genreRepository, times(1)).deleteByUUId(id);
  }

  @Test
  void should_return_not_found_when_genre_id_does_not_exist_on_deleting() throws Exception {
    UUID id = UUID.randomUUID();
    when(genreRepository.deleteByUUId(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> genreService.deleteGenreById(id))
        .isInstanceOf(NotFoundException.class);

    verify(genreRepository).deleteByUUId(id);
  }
}
