package hei.school.library.endpoint.rest.controller;

import hei.school.library.dto.ArrivalBookCopyRequest;
import hei.school.library.dto.ArrivalBookCopyResponse;
import hei.school.library.service.ArrivalBookCopyService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/arrivals/{arrivalId}/items")
@RequiredArgsConstructor
public class ArrivalBookCopyController {
  private final ArrivalBookCopyService arrivalBookCopyService;

  @GetMapping
  public ResponseEntity<List<ArrivalBookCopyResponse>> findByArrivalId(@PathVariable UUID arrivalId) {
    return ResponseEntity.status(HttpStatus.OK).body(arrivalBookCopyService.findByArrivalId(arrivalId));
  }

  @PostMapping
  public ResponseEntity<ArrivalBookCopyResponse> create(
      @PathVariable UUID arrivalId, @RequestBody ArrivalBookCopyRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(arrivalBookCopyService.create(arrivalId, request));
  }

  @DeleteMapping("/{bookCopyId}")
  public ResponseEntity<Void> delete(@PathVariable UUID arrivalId, @PathVariable UUID bookCopyId) {
    arrivalBookCopyService.delete(arrivalId, bookCopyId);
    return ResponseEntity.noContent().build();
  }
}
