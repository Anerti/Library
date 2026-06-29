package hei.school.library.service;

import hei.school.library.dto.LoginRequest;
import hei.school.library.dto.RegisterRequest;
import hei.school.library.dto.UserResponse;
import hei.school.library.entity.enums.Role;
import hei.school.library.exception.ConflictException;
import hei.school.library.exception.UnauthorizedException;
import hei.school.library.mapper.UserMapper;
import hei.school.library.repository.dao.AuthRepository;
import hei.school.library.validator.DataValidator;
import hei.school.library.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService {

  private final AuthRepository authRepository;
  private final UserMapper userMapper;
  private final UserValidator userValidator;
  private final PasswordEncoder passwordEncoder;
  private final DataValidator dataValidator;

  @Transactional
  public UserResponse create(RegisterRequest request) {
    userValidator.validateUserCreation(request);

    String encodedPassword = passwordEncoder.encode(request.getPassword());

    return authRepository
        .create(
            request.getLastName(),
            request.getFirstName(),
            request.getBirthDate(),
            request.getEmail(),
            encodedPassword,
            request.getPhone(),
            Role.CUSTOMER.name())
        .map(userMapper::toResponse)
        .orElseThrow(
            () -> new ConflictException(String.format("User email %s already used.", request.getEmail())));
  }

  @Transactional(readOnly = true)
  public UserResponse login(LoginRequest request) {
    userValidator.validateUserFetch(request);
    var user =
        authRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new UnauthorizedException("Invalid credentials."));

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new UnauthorizedException("Invalid credentials.");
    }

    return userMapper.toResponse(user);
  }
}
