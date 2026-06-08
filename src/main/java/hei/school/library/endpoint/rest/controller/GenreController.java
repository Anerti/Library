package hei.school.library.endpoint.rest.controller;
import hei.school.library.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    @GetMapping("/{genreId}")
    public ResponseEntity<?> getGenreById(
            @PathVariable UUID genreId
    ) {
        return ResponseEntity.ok(genreService.getGenreById(genreId));
    }

}
