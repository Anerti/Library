package hei.school.library.endpoint.rest.controller;

import hei.school.library.dto.LibraryListResponse;
import hei.school.library.dto.LibraryRequest;
import hei.school.library.dto.LibraryResponse;
import hei.school.library.service.LibraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

  @PostMapping
  public ResponseEntity<LibraryResponse> create(@RequestBody LibraryRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(libraryService.createLibrary(request));
  }
}
