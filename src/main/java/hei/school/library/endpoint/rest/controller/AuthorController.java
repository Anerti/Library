package hei.school.library.endpoint.rest.controller;

import hei.school.library.dto.AuthorRequest;
import hei.school.library.dto.AuthorResponse;
import hei.school.library.dto.AuthorUpdateRequest;
import hei.school.library.service.AuthorService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/authors")
public class AuthorController {
  private final AuthorService authorService;

  @GetMapping
  public ResponseEntity<List<AuthorResponse>> findAll() {
    return ResponseEntity.status(HttpStatus.OK).body(authorService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<AuthorResponse> findById(@PathVariable UUID id) {
    return ResponseEntity.status(HttpStatus.OK).body(authorService.findById(id));
  }

  @PostMapping
  public ResponseEntity<AuthorResponse> create(@RequestBody AuthorRequest authorRequest) {
    var author = authorService.create(authorRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(author);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<AuthorResponse> update(
      @PathVariable UUID id, @RequestBody AuthorUpdateRequest authorUpdateRequest) {
    var author = authorService.update(id, authorUpdateRequest);
    return ResponseEntity.status(HttpStatus.OK).body(author);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    authorService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
