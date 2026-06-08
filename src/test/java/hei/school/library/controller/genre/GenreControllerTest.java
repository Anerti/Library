package hei.school.library.controller.genre;
import hei.school.library.dto.GenreResponse;
import hei.school.library.endpoint.rest.controller.GenreController;
import hei.school.library.exception.NotFoundException;
import hei.school.library.service.GenreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GenreControllerTest {
    private MockMvc mockMvc;
    private GenreService genreService;
    private GenreController genreController;

    @BeforeEach
    void setup() {
        this.genreService = Mockito.mock(GenreService.class);
        this.genreController = new GenreController(this.genreService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.genreController).build();
    }
    @Test
    void should_get_genre_by_id() throws Exception {

        UUID id = UUID.randomUUID();

        GenreResponse response =
                new GenreResponse();
        response.setId(id);
        response.setName("Fantasy");
        response.setCreatedAt(Instant.now());
        response.setUpdatedAt(Instant.now());

        when(genreService.getGenreById(id))
                .thenReturn(response);

        mockMvc.perform(get("/genres/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name")
                        .value("Fantasy"));
    }
    @Test
    void should_return_not_found_when_genre_id_does_not_exist() throws Exception {
        when(genreService.getGenreById(any()))
                .thenThrow(new NotFoundException("The requested resource was not found"));
        UUID id = UUID.randomUUID();

        mockMvc.perform(get("/genres/{id}", id))
                .andExpect(status().isNotFound());
    }
}