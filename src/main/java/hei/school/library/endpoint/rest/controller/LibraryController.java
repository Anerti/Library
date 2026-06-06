package hei.school.library.endpoint.rest.controller;

import hei.school.library.service.LibraryService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public record LibraryController(LibraryService libraryService) {

  @GetMapping("/libraries")
  public ResponseEntity<Map<String, Object>> listLibraries(
      @RequestParam(value = "search", required = false) String search,
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "size", defaultValue = "20") int size) {

    return ResponseEntity.ok(libraryService.listLibraries(search, page, size));
  }
}
