package hei.school.library.endpoint.rest.controller;

import hei.school.library.dto.PageResponse;
import hei.school.library.dto.SaleRequest;
import hei.school.library.dto.SaleResponse;
import hei.school.library.dto.SaleUpdateRequest;
import hei.school.library.entity.enums.SaleStatus;
import hei.school.library.service.SaleService;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/libraries/{libraryId}/sales")
public class SaleController {

  private final SaleService saleService;

  @GetMapping
  public ResponseEntity<PageResponse<SaleResponse>> findAll(
      @PathVariable UUID libraryId,
      @RequestParam(required = false) SaleStatus status,
      @RequestParam(name = "customer.id", required = false) UUID customerId,
      @RequestParam(required = false) Instant from,
      @RequestParam(required = false) Instant to,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(saleService.findAll(libraryId, status, customerId, from, to, page, size));
  }

  @GetMapping("/{saleId}")
  public ResponseEntity<SaleResponse> findById(
      @PathVariable UUID libraryId, @PathVariable UUID saleId) {
    return ResponseEntity.status(HttpStatus.OK).body(saleService.findById(libraryId, saleId));
  }

  @PostMapping
  public ResponseEntity<SaleResponse> create(
      @PathVariable UUID libraryId, @RequestBody SaleRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(saleService.create(libraryId, request));
  }

  @PatchMapping("/{saleId}")
  public ResponseEntity<SaleResponse> update(
      @PathVariable UUID libraryId,
      @PathVariable UUID saleId,
      @RequestBody SaleUpdateRequest request) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(saleService.update(libraryId, saleId, request));
  }
}
