package hei.school.library.endpoint.rest.controller;

import hei.school.library.dto.GenreRequest;
import hei.school.library.dto.GenreResponse;
import hei.school.library.dto.PageResponse;
import hei.school.library.service.GenreService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/genres")
public class GenreController {
  private final GenreService genreService;

  @GetMapping("/{genreId}")
  public ResponseEntity<GenreResponse> getGenreById(@PathVariable UUID genreId) {
    return ResponseEntity.status(HttpStatus.OK).body(genreService.getGenreById(genreId));
  }

  @PostMapping
  public ResponseEntity<?> create(@RequestBody GenreRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(genreService.createGenreByName(request));
  }

  @PatchMapping("/{genreId}")
  public ResponseEntity<?> update(@PathVariable UUID genreId, @RequestBody GenreRequest request) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(genreService.updateGenreByName(genreId, request));
  }

  @DeleteMapping("/{genreId}")
  public ResponseEntity<Void> delete(@PathVariable UUID genreId) {
    genreService.deleteGenreById(genreId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping
  public ResponseEntity<PageResponse<GenreResponse>> findAll(
      @RequestParam(required = false) String search,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.status(HttpStatus.OK).body(genreService.findAll(search, page, size));
  }
}
