package hei.school.library.endpoint.rest.controller;

import hei.school.library.dto.SaleBookCopyRequest;
import hei.school.library.dto.SaleBookCopyResponse;
import hei.school.library.service.SaleBookCopyService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/sales/{saleId}/items")
public class SaleBookCopyController {

  private final SaleBookCopyService saleBookCopyService;

  @GetMapping
  public ResponseEntity<List<SaleBookCopyResponse>> findBySaleId(@PathVariable UUID saleId) {
    return ResponseEntity.status(HttpStatus.OK).body(saleBookCopyService.findBySaleId(saleId));
  }

  @PostMapping
  public ResponseEntity<SaleBookCopyResponse> create(
      @PathVariable UUID saleId, @RequestBody SaleBookCopyRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(saleBookCopyService.create(saleId, request));
  }

  @DeleteMapping("/{bookCopyId}")
  public ResponseEntity<Void> delete(@PathVariable UUID saleId, @PathVariable UUID bookCopyId) {
    saleBookCopyService.delete(saleId, bookCopyId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
