package hei.school.library.endpoint.rest.controller;
import hei.school.library.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import hei.school.library.dto.GenreRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


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

  @PostMapping
  public ResponseEntity<?> create(@RequestBody GenreRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(genreService.createGenreByName(request));
  }
}
