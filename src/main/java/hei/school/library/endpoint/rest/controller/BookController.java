package hei.school.library.endpoint.rest.controller;

import hei.school.library.dto.BookRequest;
import hei.school.library.dto.BookResponse;
import hei.school.library.service.BookService;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

  private final BookService bookService;

  @PostMapping
  public ResponseEntity<BookResponse> createBook(@RequestBody BookRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(request));
  }

  @GetMapping
  public ResponseEntity<Map<String, Object>> searchBooks(
      @RequestParam(required = false) String search,
      @RequestParam(required = false) String isbn,
      @RequestParam(required = false) UUID authorId,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "20") int size) {

    return ResponseEntity.ok(bookService.listBooks(search, isbn, authorId, page, size));
  }

  @GetMapping("/{id}")
  public ResponseEntity<BookResponse> getBookById(@PathVariable UUID id) {
    return ResponseEntity.ok(bookService.getBookById(id));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<BookResponse> updateBook(
      @PathVariable UUID id, @RequestBody BookRequest request) {
    return ResponseEntity.ok(bookService.updateBook(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<UUID> deleteBook(@PathVariable UUID id) {
    return ResponseEntity.ok(bookService.deleteBook(id));
  }
}
