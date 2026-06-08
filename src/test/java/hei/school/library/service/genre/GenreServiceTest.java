package hei.school.library.service.genre;
import hei.school.library.entity.Genre;
import hei.school.library.exception.NotFoundException;
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
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
        genreService = new GenreService(genreRepository, genreMapper, dataValidator);
    }

    @Test
    void should_get_genre_by_id() throws Exception {

        UUID id = UUID.randomUUID();
        Genre genre = Genre.builder()
                .id(id)
                .name("Fantasy")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(genreRepository.findById(any(UUID.class))).thenReturn(Optional.of(genre));

        assertEquals(genreMapper.toResponse(genre), genreService.getGenreById(id));
    }
    @Test
    void should_return_not_found_when_genre_id_does_not_exist() throws Exception {
        UUID id = UUID.randomUUID();
        when(genreRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> genreService.getGenreById(id));
    }
    @Test
    void should_return_unprocessable_entity_when_id_is_not_valid() throws Exception {
        doThrow(UnprocessableEntityException.class)
                .when(dataValidator).validateString(eq("id"), any());

        assertThrows(UnprocessableEntityException.class, () -> genreService.getGenreById(null));

    }
}