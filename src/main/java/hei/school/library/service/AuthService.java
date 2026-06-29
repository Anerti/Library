package hei.school.library.service;

import hei.school.library.dto.UserRequest;
import hei.school.library.dto.UserResponse;
import hei.school.library.entity.enums.Role;
import hei.school.library.exception.ConflictException;
import hei.school.library.mapper.UserMapper;
import hei.school.library.repository.dao.AuthRepository;
import hei.school.library.validator.DataValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService {

  private final AuthRepository authRepository;
  private final UserMapper userMapper;
  private final DataValidator dataValidator;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public UserResponse create(UserRequest request) {
    dataValidator.validateUser(request);

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
}
