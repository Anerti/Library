package hei.school.library.endpoint.rest.controller;

import hei.school.library.dto.LibraryRequest;
import hei.school.library.service.LibraryService;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/libraries")
@RestController
public record LibraryController(LibraryService libraryService) {

  @GetMapping
  public ResponseEntity<Map<String, Object>> listLibraries(
      @RequestParam(value = "search", required = false) String search,
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "size", defaultValue = "20") int size) {

    return ResponseEntity.ok(libraryService.listLibraries(search, page, size));
  }

  @PostMapping
  public ResponseEntity<?> create(@RequestBody LibraryRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(libraryService.createLibrary(request));
  }

  @PatchMapping("/{libraryId}")
  public ResponseEntity<?> update(
      @PathVariable UUID libraryId, @RequestBody LibraryRequest request) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(libraryService.updateLibrary(libraryId, request));
  }
}
