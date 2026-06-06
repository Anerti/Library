package hei.school.library.genre;
import hei.school.library.dto.genre.GenreRequest;
import hei.school.library.dto.genre.GenreResponse;
import hei.school.library.endpoint.rest.controller.library.GenreController;
import hei.school.library.exception.BadRequestException;
import hei.school.library.exception.NotFoundException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
                .thenThrow(new BadRequestException("The requested resource already exists"));

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
    @Test
    void shouldGetGenreById() throws Exception {

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
    void shouldReturnNotFoundWhenGenreIdDoesNotExist() throws Exception {
        when(genreService.getGenreById(any()))
                .thenThrow(new NotFoundException("The requested resource was not found"));
        UUID id = UUID.randomUUID();

        mockMvc.perform(get("/genres/{id}", id))
                .andExpect(status().isNotFound());
    }
    @Test
    void shouldReturnBadRequestWhenIdIsNotValid() throws Exception {
        when(genreService.createGenreByName(any(GenreRequest.class)))
                .thenThrow(new BadRequestException("The request body contains invalid JSON or a parameter is malformed"));

        String jsonBodyWithNull = "{\"name\": null}";

        mockMvc.perform(post("/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBodyWithNull))
                .andExpect(status().isBadRequest());
    }
}
