package hei.school.library.endpoint.rest.controller;

import hei.school.library.dto.*;
import hei.school.library.service.LibraryService;

import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/libraries")
@RestController
@RequiredArgsConstructor
public class LibraryController {

  private final LibraryService libraryService;

  @GetMapping
  public ResponseEntity<LibraryListResponse> listLibraries(
      @RequestParam(value = "search", required = false) String search,
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "size", defaultValue = "20") int size) {

    return ResponseEntity.status(HttpStatus.OK)
        .body(libraryService.listLibraries(search, page, size));
  }

  @GetMapping("/{libraryId}")
  public ResponseEntity<LibraryResponse> getById(@PathVariable UUID libraryId) {
    return ResponseEntity.status(HttpStatus.OK).body(libraryService.getLibrary(libraryId));
  }

  @PostMapping
  public ResponseEntity<LibraryResponse> create(@RequestBody LibraryRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(libraryService.createLibrary(request));
  }

  @DeleteMapping("/{libraryId}")
  public ResponseEntity<Void> delete(@PathVariable UUID libraryId) {
    libraryService.deleteLibrary(libraryId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping("/{libraryId}/analytics/revenue/by-genre")
  public ResponseEntity<PageResponse> getRevenueByGenre(
      @PathVariable UUID libraryId,
      @RequestParam(required = false) LocalDate from,
      @RequestParam(required = false) LocalDate to,
      @RequestParam(defaultValue = "desc") String sortOrder,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                libraryService.findRevenueByGenre(libraryId, from, to, sortOrder, page, size));
    }
}
