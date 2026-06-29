package hei.school.library.endpoint.rest.controller;

import hei.school.library.dto.PageResponse;
import hei.school.library.dto.UserResponse;
import hei.school.library.dto.UserUpdateRequest;
import hei.school.library.service.UserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  @GetMapping
  public ResponseEntity<PageResponse<UserResponse>> findAll(
      @RequestParam(required = false) String search,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.status(HttpStatus.OK).body(userService.findAll(search, page, size));
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserResponse> findById(@PathVariable UUID id) {
    return ResponseEntity.status(HttpStatus.OK).body(userService.findById(id));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<UserResponse> update(
      @PathVariable UUID id, @RequestBody UserUpdateRequest request) {
    return ResponseEntity.status(HttpStatus.OK).body(userService.update(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    userService.delete(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
