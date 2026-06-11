package hei.school.library.service.genre;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import hei.school.library.dto.GenreRequest;
import hei.school.library.dto.GenreResponse;
import hei.school.library.dto.PageResponse;
import hei.school.library.entity.Genre;
import hei.school.library.exception.ConflictException;
import hei.school.library.exception.UnprocessableEntityException;
import hei.school.library.mapper.GenreMapper;
import hei.school.library.repository.dao.GenreRepository;
import hei.school.library.service.GenreService;
import hei.school.library.validator.DataValidator;
import java.time.Instant;
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
}
