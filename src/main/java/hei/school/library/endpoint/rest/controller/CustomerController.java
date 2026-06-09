package hei.school.library.endpoint.rest.controller;

import hei.school.library.dto.CustomerResponse;
import hei.school.library.dto.PageResponse;
import hei.school.library.service.CustomerService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/customers")
public class CustomerController {

  private final CustomerService customerService;

  @GetMapping
  public ResponseEntity<PageResponse<CustomerResponse>> findAll(
      @RequestParam(required = false) String search,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.status(HttpStatus.OK).body(customerService.findAll(search, page, size));
  }

  @GetMapping("/{id}")
  public ResponseEntity<CustomerResponse> findById(@PathVariable UUID id) {
    return ResponseEntity.status(HttpStatus.OK).body(customerService.findById(id));
  }
}
