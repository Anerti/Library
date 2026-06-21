package hei.school.library.endpoint.rest.controller;

import hei.school.library.dto.CustomerRequest;
import hei.school.library.dto.CustomerResponse;
import hei.school.library.dto.CustomerUpdateRequest;
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

  @PostMapping
  public ResponseEntity<CustomerResponse> create(@RequestBody CustomerRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(customerService.create(request));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<CustomerResponse> update(
      @PathVariable UUID id, @RequestBody CustomerUpdateRequest request) {
    return ResponseEntity.status(HttpStatus.OK).body(customerService.update(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    customerService.delete(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
