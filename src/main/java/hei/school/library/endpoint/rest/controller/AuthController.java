package hei.school.library.endpoint.rest.controller;

import hei.school.library.config.JwtTokenProvider;
import hei.school.library.dto.AuthResponse;
import hei.school.library.dto.UserRequest;
import hei.school.library.dto.UserResponse;
import hei.school.library.mapper.UserMapper;
import hei.school.library.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final JwtTokenProvider tokenProvider;
  private final UserService userService;
  private final UserMapper userMapper;

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@RequestBody UserRequest request) {
    UserResponse userResponse = userService.create(request);

    String token =
        tokenProvider.generateToken(
            userResponse.getId().toString(), userResponse.getRole().name());

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            AuthResponse.builder()
                .token(token)
                .user(userMapper.toAuthUser(userResponse))
                .build());
  }
}
