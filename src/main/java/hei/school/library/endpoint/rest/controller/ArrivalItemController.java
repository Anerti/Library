package hei.school.library.endpoint.rest.controller;

import hei.school.library.dto.ArrivalItemRequest;
import hei.school.library.dto.ArrivalItemResponse;
import hei.school.library.service.ArrivalItemService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/arrivals/{arrivalId}/items")
@RequiredArgsConstructor
public class ArrivalItemController {
  private final ArrivalItemService arrivalItemService;

  @GetMapping
  public ResponseEntity<List<ArrivalItemResponse>> findByArrivalId(@PathVariable UUID arrivalId) {
    return ResponseEntity.status(HttpStatus.OK).body(arrivalItemService.findByArrivalId(arrivalId));
  }

  @PostMapping
  public ResponseEntity<ArrivalItemResponse> create(
      @PathVariable UUID arrivalId, @RequestBody ArrivalItemRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(arrivalItemService.create(arrivalId, request));
  }

  @DeleteMapping("/{bookCopyId}")
  public ResponseEntity<Void> delete(@PathVariable UUID arrivalId, @PathVariable UUID bookCopyId) {
    arrivalItemService.delete(arrivalId, bookCopyId);
    return ResponseEntity.noContent().build();
  }
}
