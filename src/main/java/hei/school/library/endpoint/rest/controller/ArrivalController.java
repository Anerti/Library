package hei.school.library.endpoint.rest.controller;

import hei.school.library.dto.ArrivalRequest;
import hei.school.library.dto.ArrivalResponse;
import hei.school.library.dto.PageResponse;
import hei.school.library.service.ArrivalService;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/libraries/{libraryId}/arrivals")
public class ArrivalController {
  private final ArrivalService arrivalService;

  @GetMapping
  public ResponseEntity<PageResponse<ArrivalResponse>> findByLibraryId(
      @PathVariable UUID libraryId,
      @RequestParam(required = false) LocalDateTime from,
      @RequestParam(required = false) LocalDateTime to,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "20") int size) {

    return ResponseEntity.status(HttpStatus.OK)
        .body(arrivalService.findByLibraryId(libraryId, from, to, page, size));
  }

  @GetMapping("/{arrivalId}")
  public ResponseEntity<ArrivalResponse> findById(
      @PathVariable UUID libraryId, @PathVariable UUID arrivalId) {

    return ResponseEntity.status(HttpStatus.OK).body(arrivalService.findById(libraryId, arrivalId));
  }

  @PostMapping
  public ResponseEntity<ArrivalResponse> create(
      @PathVariable UUID libraryId, @RequestBody ArrivalRequest arrivalRequest) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(arrivalService.create(libraryId, arrivalRequest));
  }
}
