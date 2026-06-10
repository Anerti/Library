package hei.school.library.endpoint.rest.controller;

import hei.school.library.dto.BookCopyResponse;
import hei.school.library.dto.PageResponse;
import hei.school.library.entity.enums.BookCopyFormat;
import hei.school.library.entity.enums.BookCopyStatus;
import hei.school.library.service.BookCopyService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/libraries/{libraryId}/copies")
@RequiredArgsConstructor
@RestController
public class BookCopyController {
  private final BookCopyService bookCopyService;

  @GetMapping
  public ResponseEntity<PageResponse<BookCopyResponse>> findByFilters(
      @PathVariable UUID libraryId,
      @RequestParam(required = false) BookCopyStatus status,
      @RequestParam(required = false) BookCopyFormat format,
      @RequestParam(required = false) UUID bookId,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "20") int size) {

    return ResponseEntity.status(HttpStatus.OK)
        .body(bookCopyService.findByFilters(libraryId, status, format, bookId, page, size));
  }

  @GetMapping("/{copyId}")
  public ResponseEntity<BookCopyResponse> findById(
      @PathVariable UUID libraryId, @PathVariable UUID copyId) {

    return ResponseEntity.status(HttpStatus.OK).body(bookCopyService.findById(libraryId, copyId));
  }
}
