package hei.school.library.genre;
import hei.school.library.dto.genre.GenreRequest;
import hei.school.library.dto.genre.GenreResponse;
import hei.school.library.endpoint.rest.controller.library.GenreController;
import hei.school.library.exception.BadRequestException;
import hei.school.library.service.GenreService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GenreController.class)
public class GenreControllerTest {
    @MockBean
    private GenreService genreService;

    @Autowired
    private MockMvc mockMvc;

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
    void shouldReturnBadRequestWhenGenreAlreadyExists() throws Exception {
        when(genreService.createGenreByName(any(GenreRequest.class)))
                .thenThrow(new BadRequestException("Genre already exists"));

        mockMvc.perform(post("/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "name":"Fantasy"
                        }
                    """)
                )
                .andExpect(status().isBadRequest());
    }
}
