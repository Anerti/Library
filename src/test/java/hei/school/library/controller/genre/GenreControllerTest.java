package hei.school.library.controller.genre;
import hei.school.library.dto.GenreRequest;
import hei.school.library.dto.GenreResponse;
import hei.school.library.endpoint.rest.controller.GenreController;
import hei.school.library.exception.BadRequestException;
import hei.school.library.exception.ConflictException;
import hei.school.library.service.GenreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    void shouldCreateGenre() throws Exception {

        UUID id = UUID.randomUUID();

        GenreResponse response =
                new GenreResponse();
        response.setId(id);
        response.setName("Fantasy");
        response.setCreatedAt(Instant.now());
        response.setUpdatedAt(Instant.now());

        when(genreService.createGenreByName(any(GenreRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "name":"Fantasy"
                        }
                    """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name")
                        .value("Fantasy"));
    }
    @Test
    void shouldReturnConflictWhenGenreAlreadyExists() throws Exception {
        shouldCreateGenre();
        when(genreService.createGenreByName(any(GenreRequest.class)))
                .thenThrow(new ConflictException("The requested resource already exists"));

        mockMvc.perform(post("/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "name":"Fantasy"
                        }
                    """)
                )
                .andExpect(status().isConflict());
    }
    @Test
    void shouldReturnBadRequestWhenRequestIsNotValid() throws Exception {
        when(genreService.createGenreByName(any(GenreRequest.class)))
                .thenThrow(new BadRequestException("The request body contains invalid JSON or a parameter is malformed"));

        mockMvc.perform(post("/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {

                        }
                    """)
                )
                .andExpect(status().isBadRequest());
    }
}
