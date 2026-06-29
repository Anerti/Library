package hei.school.library.service.auth;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import hei.school.library.dto.LoginRequest;
import hei.school.library.dto.UserResponse;
import hei.school.library.entity.User;
import hei.school.library.entity.enums.Role;
import hei.school.library.exception.UnauthorizedException;
import hei.school.library.exception.UnprocessableEntityException;
import hei.school.library.mapper.PaginationMapper;
import hei.school.library.mapper.UserMapper;
import hei.school.library.repository.dao.AuthRepository;
import hei.school.library.service.AuthService;
import hei.school.library.validator.DataValidator;
import hei.school.library.validator.UserValidator;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class PostAuthLoginServiceTest {

  @Mock private AuthRepository authRepository;
  @Mock private UserValidator userValidator;
  @Mock private PasswordEncoder passwordEncoder;

  private AuthService authService;
  private User user;
  private LoginRequest validRequest;

  @BeforeEach
  void setUp() {
    authService =
        new AuthService(authRepository, new UserMapper(new PaginationMapper()), userValidator, passwordEncoder, new DataValidator());

    UUID id = UUID.randomUUID();
    user =
        new User(
            id,
            "Dupont",
            "Marie",
            LocalDate.of(1995, 3, 10),
            "marie@mail.com",
            "encoded-secret",
            "+261****4567",
            Role.CUSTOMER,
            Instant.now(),
            Instant.now());

    validRequest = new LoginRequest("marie@mail.com", "Str0ng!Passphrase1");
  }

  @Test
  @DisplayName("login: should return UserResponse when credentials are valid")
  void login_shouldReturnUserResponse() {
    when(authRepository.findByEmail("marie@mail.com")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("Str0ng!Passphrase1", "encoded-secret")).thenReturn(true);

    UserResponse result = authService.login(validRequest);

    assertThat(result.getLastName()).isEqualTo("Dupont");
    assertThat(result.getEmail()).isEqualTo("marie@mail.com");
    assertThat(result.getRole()).isEqualTo(Role.CUSTOMER);
    verify(authRepository).findByEmail("marie@mail.com");
    verify(passwordEncoder).matches("Str0ng!Passphrase1", "encoded-secret");
  }

  @Test
  @DisplayName("login: should throw UnauthorizedException when email not found")
  void login_shouldThrow_whenEmailNotFound() {
    when(authRepository.findByEmail("marie@mail.com")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authService.login(validRequest))
        .isInstanceOf(UnauthorizedException.class)
        .hasMessage("Invalid email or password.");
  }

  @Test
  @DisplayName("login: should throw UnauthorizedException when password does not match")
  void login_shouldThrow_whenPasswordMismatch() {
    when(authRepository.findByEmail("marie@mail.com")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("Str0ng!Passphrase1", "encoded-secret")).thenReturn(false);

    assertThatThrownBy(() -> authService.login(validRequest))
        .isInstanceOf(UnauthorizedException.class)
        .hasMessage("Invalid email or password.");
  }

  @Test
  @DisplayName("login: should throw UnprocessableEntityException when email is null")
  void login_shouldThrow_whenEmailNull() {
    LoginRequest request = new LoginRequest(null, "Str0ng!Passphrase1");

    assertThatThrownBy(() -> authService.login(request))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("email is required");
  }

  @Test
  @DisplayName("login: should throw UnprocessableEntityException when password is null")
  void login_shouldThrow_whenPasswordNull() {
    LoginRequest request = new LoginRequest("marie@mail.com", null);

    assertThatThrownBy(() -> authService.login(request))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("password is required");
  }

  @Test
  @DisplayName("login: should throw UnprocessableEntityException when email format is invalid")
  void login_shouldThrow_whenEmailInvalidFormat() {
    LoginRequest request = new LoginRequest("not-an-email", "Str0ng!Passphrase1");

    assertThatThrownBy(() -> authService.login(request))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("Invalid email format");
  }
}
