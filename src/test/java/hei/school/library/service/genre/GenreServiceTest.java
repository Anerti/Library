package hei.school.library.service.genre;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import hei.school.library.dto.GenreRequest;
import hei.school.library.dto.GenreResponse;
import hei.school.library.dto.PageResponse;
import hei.school.library.entity.Genre;
import hei.school.library.exception.ConflictException;
import hei.school.library.exception.NotFoundException;
import hei.school.library.exception.UnprocessableEntityException;
import hei.school.library.mapper.GenreMapper;
import hei.school.library.repository.dao.GenreRepository;
import hei.school.library.service.GenreService;
import hei.school.library.validator.DataValidator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
  void should_get_genre_by_id() {
    UUID id = UUID.randomUUID();
    String name = "Fantasy";

    Genre genre = Genre.builder().id(id).name(name).build();
    GenreResponse expectedResponse = GenreResponse.builder().id(id).name(name).build();

    when(genreRepository.findById(id)).thenReturn(Optional.of(genre));
    when(genreMapper.toResponse(genre)).thenReturn(expectedResponse);

    GenreResponse actualResponse = genreService.getGenreById(id);
    assertEquals(expectedResponse, actualResponse);
    verify(genreRepository, times(1)).findById(id);
    verify(genreMapper, times(1)).toResponse(genre);
  }

  @Test
  void should_return_not_found_when_genre_id_does_not_exist() {
    UUID id = UUID.randomUUID();
    when(genreRepository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> genreService.getGenreById(id))
        .isInstanceOf(NotFoundException.class);
    verify(genreRepository).findById(id);
    verify(genreMapper, never()).toResponse(any());
  }

  @Test
  void should_create_genre() throws Exception {

    UUID id = UUID.randomUUID();
    String genreName = "Fantasy";

    Genre genre = Genre.builder().id(id).name(genreName).build();

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
  void should_return_all_genres_when_search_is_null_or_empty() {
    int page = 1;
    int size = 10;
    PageRequest pageable = PageRequest.of(page - 1, size);
    List<Genre> genreList = List.of(new Genre(), new Genre());
    Page<Genre> genrePage = new PageImpl<>(genreList, pageable, genreList.size());

    PageResponse<GenreResponse> expectedResponse = new PageResponse<>();

    when(genreRepository.findAll(pageable)).thenReturn(genrePage);
    when(genreMapper.toPageResponse(genrePage, page, size)).thenReturn(expectedResponse);
    PageResponse<GenreResponse> actualResponse = genreService.findAll(null, page, size);
    assertNotNull(actualResponse);
    verify(genreRepository, times(1)).findAll(pageable);
    verify(genreRepository, never()).findBySearch(anyString(), any());
    verify(genreMapper, times(1)).toPageResponse(genrePage, page, size);
  }

  @Test
  void should_return_filtered_genres_when_search_is_provided() {
    String search = "Fan";
    int page = 1;
    int size = 10;
    PageRequest pageable = PageRequest.of(page - 1, size);

    List<Genre> genreList = List.of(new Genre());
    Page<Genre> genrePage = new PageImpl<>(genreList, pageable, genreList.size());

    PageResponse<GenreResponse> expectedResponse = new PageResponse<>();
    when(genreRepository.findBySearch(eq(search), eq(pageable))).thenReturn(genrePage);
    when(genreMapper.toPageResponse(genrePage, page, size)).thenReturn(expectedResponse);
    PageResponse<GenreResponse> actualResponse = genreService.findAll(search, page, size);
    assertNotNull(actualResponse);
    verify(genreRepository, times(1)).findBySearch(eq(search), eq(pageable));
    verify(genreRepository, never()).findAll(any(Pageable.class));
    verify(genreMapper, times(1)).toPageResponse(genrePage, page, size);
  }

  @Test
  void should_update_genre_by_id() throws Exception {
    UUID id = UUID.randomUUID();
    String newName = "Action";

    GenreRequest request = new GenreRequest();
    request.setName(newName);

    Genre updatedGenreFromDb = new Genre();
    updatedGenreFromDb.setId(id);
    updatedGenreFromDb.setName(newName);

    GenreResponse expectedResponse = new GenreResponse();
    expectedResponse.setId(id);
    expectedResponse.setName(newName);
    doNothing().when(dataValidator).validateName("name", newName);
    when(genreRepository.updateGenreName(id, newName)).thenReturn(Optional.of(updatedGenreFromDb));
    when(genreMapper.toResponse(updatedGenreFromDb)).thenReturn(expectedResponse);
    GenreResponse actualResponse = genreService.updateGenreByName(id, request);
    assertNotNull(actualResponse);
    assertEquals(newName, actualResponse.getName());
    assertEquals(id, actualResponse.getId());
    verify(dataValidator, times(1)).validateName("name", newName);
    verify(genreRepository, times(1)).updateGenreName(id, newName);
    verify(genreMapper, times(1)).toResponse(updatedGenreFromDb);
  }

  @Test
  void should_return_not_found_when_genre_id_does_not_exist_on_updating() throws Exception {
    UUID id = UUID.randomUUID();
    GenreRequest request = GenreRequest.builder().name("Fantasy").build();
    when(genreRepository.updateGenreName(id, request.getName())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> genreService.updateGenreByName(id, request))
        .isInstanceOf(NotFoundException.class);

    verify(genreRepository).updateGenreName(id, request.getName());
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
