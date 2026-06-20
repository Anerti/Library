package hei.school.library.endpoint.rest.controller;

import hei.school.library.dto.ArrivalItemResponse;
import hei.school.library.service.ArrivalItemService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/arrivals/{arrivalId}/items")
@RequiredArgsConstructor
public class ArrivalItemController {
  private final ArrivalItemService arrivalItemService;

  @GetMapping
  public ResponseEntity<List<ArrivalItemResponse>> findByArrivalId(@PathVariable UUID arrivalId) {
    return ResponseEntity.status(HttpStatus.OK).body(arrivalItemService.findByArrivalId(arrivalId));
  }
}
