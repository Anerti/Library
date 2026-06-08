package hei.school.library.service.genre;

import hei.school.library.dto.GenreRequest;
import hei.school.library.entity.Genre;
import hei.school.library.exception.ConflictException;
import hei.school.library.exception.UnprocessableEntityException;
import hei.school.library.mapper.GenreMapper;
import hei.school.library.repository.dao.GenreRepository;
import hei.school.library.service.GenreService;
import hei.school.library.validator.DataValidator;
import hei.school.library.validator.GenreValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Instant;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GenreServiceTest {
    @Mock
    private GenreRepository genreRepository;
    @Mock
    private DataValidator dataValidator;
    @Mock
    private GenreMapper genreMapper;
    @Mock
    private GenreValidator genreValidator;

    private GenreService genreService;

    @BeforeEach
    void setUp() {
        genreService = new GenreService(genreRepository, genreMapper, genreValidator, dataValidator);
    }

    @Test
    void should_create_genre() throws Exception {

        UUID id = UUID.randomUUID();
        Genre genre = Genre.builder()
                .id(id)
                .name("Fantasy")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(genreRepository.save(any(Genre.class))).thenReturn(genre);

        assertEquals(genreMapper.toResponse(genre), genreService.createGenreByName(
                GenreRequest.builder()
                        .name("Fantasy")
                        .build()));
    }
    @Test
    void should_return_conflict_when_genre_already_exists() throws Exception {
        when(genreRepository.existsByNameIgnoreCase("Fantasy")).thenReturn(true);
        doThrow(ConflictException.class).when(genreValidator).isExistByName(true);
        assertThrows(ConflictException.class, () -> genreService.createGenreByName(
                GenreRequest.builder()
                        .name("Fantasy")
                        .build()));
    }
    @Test
    void should_return_unprocessable_entity_exception_when_request_is_not_valid() throws Exception {
        doThrow(UnprocessableEntityException.class)
                .when(dataValidator).validateString("name", null);

        assertThrows(UnprocessableEntityException.class, () -> genreService.createGenreByName(
                GenreRequest.builder()
                        .name(null)
                        .build()));

    }
}