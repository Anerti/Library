package hei.school.library.endpoint.rest.controller;

import hei.school.library.entity.enums.BookCopyFormat;
import hei.school.library.service.BookCopyService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class GetStockBookCopyController {
  private final BookCopyService bookCopyService;

  @GetMapping("/books/{bookId}/stock")
  public ResponseEntity<Long> getStock(
      @PathVariable UUID bookId,
      @RequestParam(required = false) BookCopyFormat format,
      @RequestParam(required = false) UUID libraryId) {
    return ResponseEntity.ok(bookCopyService.getStock(bookId, format, libraryId));
  }
}
