package hei.school.library.endpoint.rest.controller;

import hei.school.library.dto.BookRequest;
import hei.school.library.dto.BookResponse;
import hei.school.library.dto.BookUpdateRequest;
import hei.school.library.dto.PageResponse;
import hei.school.library.service.BookService;
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
  public ResponseEntity<PageResponse<BookResponse>> searchBooks(
      @RequestParam(required = false) String search,
      @RequestParam(required = false) String isbn,
      @RequestParam(required = false) String lastName,
      @RequestParam(required = false) String genreName,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "20") int size) {

    return ResponseEntity.ok(bookService.listBooks(search, isbn, lastName, genreName, page, size));
  }

  @GetMapping("/{id}")
  public ResponseEntity<BookResponse> getBookById(@PathVariable UUID id) {
    return ResponseEntity.status(HttpStatus.OK).body(bookService.getBookById(id));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<BookResponse> updateBook(
      @PathVariable UUID id, @RequestBody BookUpdateRequest request) {
    return ResponseEntity.ok(bookService.updateBook(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteBook(@PathVariable UUID id) {
    bookService.deleteBook(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
