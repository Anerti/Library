package hei.school.library.service.auth;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import hei.school.library.dto.UserRequest;
import hei.school.library.dto.UserResponse;
import hei.school.library.entity.User;
import hei.school.library.entity.enums.Role;
import hei.school.library.exception.ConflictException;
import hei.school.library.exception.UnprocessableEntityException;
import hei.school.library.mapper.PaginationMapper;
import hei.school.library.mapper.UserMapper;
import hei.school.library.repository.dao.AuthRepository;
import hei.school.library.service.AuthService;
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
class PostAuthServiceTest {

  @Mock private AuthRepository authRepository;
  @Mock private UserValidator userValidator;
  @Mock private PasswordEncoder passwordEncoder;

  private AuthService authService;

  private User user;
  private UserRequest validRequest;

  @BeforeEach
  void setUp() {
    authService = new AuthService(authRepository, new UserMapper(new PaginationMapper()), userValidator, passwordEncoder);

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

    validRequest =
        new UserRequest(
            "Dupont",
            "Marie",
            LocalDate.of(1995, 3, 10),
            "marie@mail.com",
            "secret",
            "+261****4567");
  }

  @Test
  @DisplayName("create: should save and return DTO")
  void create_shouldSaveAndReturnDto() {
    when(passwordEncoder.encode(validRequest.getPassword())).thenReturn("encoded-secret");
    when(authRepository.create(any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(Optional.of(user));

    UserResponse result = authService.create(validRequest);

    assertThat(result.getLastName()).isEqualTo("Dupont");
    assertThat(result.getEmail()).isEqualTo("marie@mail.com");
    verify(userValidator).validateUserCreation(validRequest);
    verify(passwordEncoder).encode("secret");
  }

  @Test
  @DisplayName("create: should throw ConflictException when email already exists")
  void create_shouldThrow_whenDuplicateEmail() {
    when(passwordEncoder.encode(validRequest.getPassword())).thenReturn("encoded-secret");
    when(authRepository.create(any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> authService.create(validRequest))
        .isInstanceOf(ConflictException.class)
        .hasMessageContaining("marie@mail.com");
  }

  @Test
  @DisplayName("create: should throw UnprocessableEntityException when lastName is missing")
  void create_shouldThrow_whenLastNameMissing() {
    UserRequest invalidRequest =
        new UserRequest(null, "Marie", LocalDate.of(1995, 3, 10), "marie@mail.com", null, null);

    doThrow(new UnprocessableEntityException("lastName is required."))
        .when(userValidator)
        .validateUserCreation(invalidRequest);

    assertThatThrownBy(() -> authService.create(invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("lastName is required.");
  }

  @Test
  @DisplayName("create: should throw UnprocessableEntityException when lastName is blank")
  void create_shouldThrow_whenLastNameBlank() {
    UserRequest invalidRequest =
        new UserRequest("", "Marie", LocalDate.of(1995, 3, 10), "marie@mail.com", null, null);

    doThrow(new UnprocessableEntityException("lastName is required."))
        .when(userValidator)
        .validateUserCreation(invalidRequest);

    assertThatThrownBy(() -> authService.create(invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("lastName is required.");
  }

  @Test
  @DisplayName("create: should throw UnprocessableEntityException when lastName contains numbers")
  void create_shouldThrow_whenLastNameHasInvalidChars() {
    UserRequest invalidRequest =
        new UserRequest(
            "Dupont123", "Marie", LocalDate.of(1995, 3, 10), "marie@mail.com", null, null);

    doThrow(new UnprocessableEntityException("lastName field contain forbidden characters."))
        .when(userValidator)
        .validateUserCreation(invalidRequest);

    assertThatThrownBy(() -> authService.create(invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("forbidden");
  }

  @Test
  @DisplayName(
      "create: should throw UnprocessableEntityException when lastName exceeds 100 characters")
  void create_shouldThrow_whenLastNameTooLong() {
    UserRequest invalidRequest =
        new UserRequest(
            "D".repeat(101), "Marie", LocalDate.of(1995, 3, 10), "marie@mail.com", null, null);

    doThrow(new UnprocessableEntityException("lastName cannot be longer than 100 characters."))
        .when(userValidator)
        .validateUserCreation(invalidRequest);

    assertThatThrownBy(() -> authService.create(invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("100 characters");
  }

  @Test
  @DisplayName("create: should throw UnprocessableEntityException when firstName is missing")
  void create_shouldThrow_whenFirstNameMissing() {
    UserRequest invalidRequest =
        new UserRequest("Dupont", null, LocalDate.of(1995, 3, 10), "marie@mail.com", null, null);

    doThrow(new UnprocessableEntityException("firstName is required."))
        .when(userValidator)
        .validateUserCreation(invalidRequest);

    assertThatThrownBy(() -> authService.create(invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("firstName is required.");
  }

  @Test
  @DisplayName("create: should throw UnprocessableEntityException when firstName is blank")
  void create_shouldThrow_whenFirstNameBlank() {
    UserRequest invalidRequest =
        new UserRequest("Dupont", "", LocalDate.of(1995, 3, 10), "marie@mail.com", null, null);

    doThrow(new UnprocessableEntityException("firstName is required."))
        .when(userValidator)
        .validateUserCreation(invalidRequest);

    assertThatThrownBy(() -> authService.create(invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("firstName is required.");
  }

  @Test
  @DisplayName("create: should throw UnprocessableEntityException when firstName contains numbers")
  void create_shouldThrow_whenFirstNameHasInvalidChars() {
    UserRequest invalidRequest =
        new UserRequest(
            "Dupont", "Marie123", LocalDate.of(1995, 3, 10), "marie@mail.com", null, null);

    doThrow(new UnprocessableEntityException("firstName field contain forbidden characters."))
        .when(userValidator)
        .validateUserCreation(invalidRequest);

    assertThatThrownBy(() -> authService.create(invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("forbidden");
  }

  @Test
  @DisplayName(
      "create: should throw UnprocessableEntityException when firstName exceeds 100 characters")
  void create_shouldThrow_whenFirstNameTooLong() {
    UserRequest invalidRequest =
        new UserRequest(
            "Dupont", "M".repeat(101), LocalDate.of(1995, 3, 10), "marie@mail.com", null, null);

    doThrow(new UnprocessableEntityException("firstName cannot be longer than 100 characters."))
        .when(userValidator)
        .validateUserCreation(invalidRequest);

    assertThatThrownBy(() -> authService.create(invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("100 characters");
  }

  @Test
  @DisplayName("create: should throw UnprocessableEntityException when email has invalid format")
  void create_shouldThrow_whenEmailInvalidFormat() {
    UserRequest invalidRequest =
        new UserRequest("Dupont", "Marie", LocalDate.of(1995, 3, 10), "not-an-email", null, null);

    doThrow(new UnprocessableEntityException("Invalid email format"))
        .when(userValidator)
        .validateUserCreation(invalidRequest);

    assertThatThrownBy(() -> authService.create(invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("Invalid email format");
  }

  @Test
  @DisplayName(
      "create: should throw UnprocessableEntityException when email exceeds 100 characters")
  void create_shouldThrow_whenEmailTooLong() {
    UserRequest invalidRequest =
        new UserRequest(
            "Dupont", "Marie", LocalDate.of(1995, 3, 10), "m".repeat(90) + "@mail.com", null, null);

    doThrow(new UnprocessableEntityException("email cannot be longer than 100 characters."))
        .when(userValidator)
        .validateUserCreation(invalidRequest);

    assertThatThrownBy(() -> authService.create(invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("100 characters");
  }

  @Test
  @DisplayName(
      "create: should throw UnprocessableEntityException when email contains forbidden characters")
  void create_shouldThrow_whenEmailHasInvalidChars() {
    UserRequest invalidRequest =
        new UserRequest(
            "Dupont", "Marie", LocalDate.of(1995, 3, 10), "marie @mail.com", null, null);

    doThrow(
            new UnprocessableEntityException(
                "Invalid input for email: 'marie @mail.com' only a-zA-Z0-9@_.- characters are"
                    + " allowed."))
        .when(userValidator)
        .validateUserCreation(invalidRequest);

    assertThatThrownBy(() -> authService.create(invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("Invalid input for email");
  }

  @Test
  @DisplayName("create: should throw UnprocessableEntityException when email is blank")
  void create_shouldThrow_whenEmailBlank() {
    UserRequest invalidRequest =
        new UserRequest("Dupont", "Marie", LocalDate.of(1995, 3, 10), "", null, null);

    doThrow(new UnprocessableEntityException("Invalid input for email"))
        .when(userValidator)
        .validateUserCreation(invalidRequest);

    assertThatThrownBy(() -> authService.create(invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("Invalid input for email");
  }

  @Test
  @DisplayName("create: should throw UnprocessableEntityException when birthDate is null")
  void create_shouldThrow_whenBirthDateNull() {
    UserRequest invalidRequest =
        new UserRequest("Dupont", "Marie", null, "marie@mail.com", null, null);

    doThrow(new UnprocessableEntityException("birthDate is required."))
        .when(userValidator)
        .validateUserCreation(invalidRequest);

    assertThatThrownBy(() -> authService.create(invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("birthDate is required.");
  }

  @Test
  @DisplayName("create: should throw UnprocessableEntityException when birthDate is in the future")
  void create_shouldThrow_whenBirthDateInFuture() {
    UserRequest futureRequest =
        new UserRequest(
            "Dupont", "Marie", LocalDate.now().plusDays(1), "future@mail.com", null, null);

    doThrow(new UnprocessableEntityException("birthDate cannot be in the future."))
        .when(userValidator)
        .validateUserCreation(futureRequest);

    assertThatThrownBy(() -> authService.create(futureRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("birthDate cannot be in the future.");
  }
}
