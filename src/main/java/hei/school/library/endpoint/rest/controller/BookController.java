package hei.school.library.endpoint.rest.controller;

import hei.school.library.dto.*;
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
      @RequestParam(required = false) String title,
      @RequestParam(required = false) String publisher,
      @RequestParam(required = false) String isbn,
      @RequestParam(required = false) String lastName,
      @RequestParam(required = false) String genre,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "20") int size) {

    return ResponseEntity.status(HttpStatus.OK)
        .body(bookService.listBooks(title, publisher, isbn, lastName, genre, page, size));
  }

  @GetMapping("/{id}")
  public ResponseEntity<BookResponse> getBookById(@PathVariable UUID id) {
    return ResponseEntity.status(HttpStatus.OK).body(bookService.getBookById(id));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<BookResponse> updateBook(
      @PathVariable UUID id, @RequestBody BookUpdateRequest request) {
    return ResponseEntity.status(HttpStatus.OK).body(bookService.updateBook(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteBook(@PathVariable UUID id) {
    bookService.deleteBook(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
    @PostMapping("/{bookId}/verify")
    public ResponseEntity<VerifyBookResponse> verifyBook(
            @PathVariable UUID bookId) {

        return ResponseEntity.ok(bookService.verifyBook(bookId));
    }
}
