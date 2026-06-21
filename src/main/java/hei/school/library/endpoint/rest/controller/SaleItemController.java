package hei.school.library.endpoint.rest.controller;

import hei.school.library.dto.SaleItemResponse;
import hei.school.library.service.SaleItemService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/sales/{saleId}/items")
public class SaleItemController {

  private final SaleItemService saleItemService;

  @GetMapping
  public ResponseEntity<List<SaleItemResponse>> findBySaleId(@PathVariable UUID saleId) {
    return ResponseEntity.status(HttpStatus.OK).body(saleItemService.findBySaleId(saleId));
  }
}
