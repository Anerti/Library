package hei.school.library.endpoint.rest.controller;

import hei.school.library.dto.AuthorRequest;
import hei.school.library.dto.AuthorResponse;
import hei.school.library.dto.AuthorUpdateRequest;
import hei.school.library.dto.PageResponse;
import hei.school.library.service.AuthorService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/authors")
public class AuthorController {
  private final AuthorService authorService;

  @GetMapping
  public ResponseEntity<PageResponse<AuthorResponse>> findAll(
      @RequestParam(required = false) String search,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.status(HttpStatus.OK).body(authorService.findAll(search, page, size));
  }

  @GetMapping("/{id}")
  public ResponseEntity<AuthorResponse> findById(@PathVariable UUID id) {
    return ResponseEntity.status(HttpStatus.OK).body(authorService.findById(id));
  }

  @PostMapping
  public ResponseEntity<AuthorResponse> create(@RequestBody AuthorRequest authorRequest) {
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(authorService.create(authorRequest));
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
