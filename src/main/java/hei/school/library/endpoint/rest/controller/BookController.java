package hei.school.library.endpoint.rest.controller;

import hei.school.library.dto.BookRequest;
import hei.school.library.dto.BookResponse;
import hei.school.library.service.BookService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

  private final BookService bookService;

  @PostMapping
  public ResponseEntity<BookResponse> createBook(@RequestBody BookRequest request) {
    BookResponse response = bookService.createBook(request);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<BookResponse>> getAllBooks() {
    return ResponseEntity.ok(bookService.getAllBooks());
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
  public ResponseEntity<Void> deleteBook(@PathVariable UUID id) {
    bookService.deleteBook(id);
    return ResponseEntity.noContent().build();
  }
}
