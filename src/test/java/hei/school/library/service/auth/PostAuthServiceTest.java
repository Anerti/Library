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
        new UserRequest(null, "Marie", LocalDate.of(1995, 3, 10), "marie@mail.com", null, null, null);

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
        new UserRequest("", "Marie", LocalDate.of(1995, 3, 10), "marie@mail.com", null, null, null);

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
            "Dupont123", "Marie", LocalDate.of(1995, 3, 10), "marie@mail.com", null, null, null);

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
            "D".repeat(101), "Marie", LocalDate.of(1995, 3, 10), "marie@mail.com", null, null, null);

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
        new UserRequest("Dupont", null, LocalDate.of(1995, 3, 10), "marie@mail.com", null, null, null);

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
        new UserRequest("Dupont", "", LocalDate.of(1995, 3, 10), "marie@mail.com", null, null, null);

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
            "Dupont", "Marie123", LocalDate.of(1995, 3, 10), "marie@mail.com", null, null, null);

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
            "Dupont", "M".repeat(101), LocalDate.of(1995, 3, 10), "marie@mail.com", null, null, null);

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
        new UserRequest("Dupont", "Marie", LocalDate.of(1995, 3, 10), "not-an-email", null, null, null);

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
            "Dupont", "Marie", LocalDate.of(1995, 3, 10), "m".repeat(90) + "@mail.com", null, null, null);

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
            "Dupont", "Marie", LocalDate.of(1995, 3, 10), "marie @mail.com", null, null, null);

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
        new UserRequest("Dupont", "Marie", LocalDate.of(1995, 3, 10), "", null, null, null);

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
        new UserRequest("Dupont", "Marie", null, "marie@mail.com", null, null, null);

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
            "Dupont", "Marie", LocalDate.now().plusDays(1), "future@mail.com", null, null, null);

    doThrow(new UnprocessableEntityException("birthDate cannot be in the future."))
        .when(userValidator)
        .validateUserCreation(futureRequest);

    assertThatThrownBy(() -> authService.create(futureRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("birthDate cannot be in the future.");
  }

  @Test
  @DisplayName("create: should throw UnprocessableEntityException when age is under 12")
  void create_shouldThrow_whenAgeUnder12() {
    UserRequest youngRequest =
        new UserRequest(
            "Dupont", "Marie", LocalDate.now().minusYears(11), "young@mail.com", null, null, null);

    doThrow(new UnprocessableEntityException("You must be at least 12 years old to create an account."))
        .when(userValidator)
        .validateUserCreation(youngRequest);

    assertThatThrownBy(() -> authService.create(youngRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("at least 12 years old");
  }

  @Test
  @DisplayName("create: should throw UnprocessableEntityException when password is null")
  void create_shouldThrow_whenPasswordNull() {
    UserRequest invalidRequest =
        new UserRequest("Dupont", "Marie", LocalDate.of(1995, 3, 10), "pw-null@mail.com", null, "Str0ng!Passphrase", null);

    doThrow(new UnprocessableEntityException("password is required and cannot be blank."))
        .when(userValidator)
        .validateUserCreation(invalidRequest);

    assertThatThrownBy(() -> authService.create(invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("password is required");
  }

  @Test
  @DisplayName("create: should throw UnprocessableEntityException when password is blank")
  void create_shouldThrow_whenPasswordBlank() {
    UserRequest invalidRequest =
        new UserRequest("Dupont", "Marie", LocalDate.of(1995, 3, 10), "pw-blank@mail.com", "", "", null);

    doThrow(new UnprocessableEntityException("password is required and cannot be blank."))
        .when(userValidator)
        .validateUserCreation(invalidRequest);

    assertThatThrownBy(() -> authService.create(invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("password is required");
  }

  @Test
  @DisplayName("create: should throw UnprocessableEntityException when password is too short")
  void create_shouldThrow_whenPasswordTooShort() {
    UserRequest invalidRequest =
        new UserRequest("Dupont", "Marie", LocalDate.of(1995, 3, 10), "pw-short@mail.com", "Short1!x", "Short1!x", null);

    doThrow(new UnprocessableEntityException("password must be at least 12 characters."))
        .when(userValidator)
        .validateUserCreation(invalidRequest);

    assertThatThrownBy(() -> authService.create(invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("at least 12 characters");
  }

  @Test
  @DisplayName("create: should throw UnprocessableEntityException when password has no uppercase")
  void create_shouldThrow_whenPasswordNoUppercase() {
    UserRequest invalidRequest =
        new UserRequest("Dupont", "Marie", LocalDate.of(1995, 3, 10), "pw-noupper@mail.com", "lowercase1!phrase", "lowercase1!phrase", null);

    doThrow(new UnprocessableEntityException("Password must contain at least one uppercase character."))
        .when(userValidator)
        .validateUserCreation(invalidRequest);

    assertThatThrownBy(() -> authService.create(invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("uppercase");
  }

  @Test
  @DisplayName("create: should throw UnprocessableEntityException when password has no lowercase")
  void create_shouldThrow_whenPasswordNoLowercase() {
    UserRequest invalidRequest =
        new UserRequest("Dupont", "Marie", LocalDate.of(1995, 3, 10), "pw-nolower@mail.com", "UPPERCASE1!PHRASE", "UPPERCASE1!PHRASE", null);

    doThrow(new UnprocessableEntityException("Password must contain at least one lowercase character."))
        .when(userValidator)
        .validateUserCreation(invalidRequest);

    assertThatThrownBy(() -> authService.create(invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("lowercase");
  }

  @Test
  @DisplayName("create: should throw UnprocessableEntityException when password has no digit")
  void create_shouldThrow_whenPasswordNoDigit() {
    UserRequest invalidRequest =
        new UserRequest("Dupont", "Marie", LocalDate.of(1995, 3, 10), "pw-nodigit@mail.com", "NoDigit!Passphrase", "NoDigit!Passphrase", null);

    doThrow(new UnprocessableEntityException("Password must contain at least one digits."))
        .when(userValidator)
        .validateUserCreation(invalidRequest);

    assertThatThrownBy(() -> authService.create(invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("digits");
  }

  @Test
  @DisplayName("create: should throw UnprocessableEntityException when password has no special character")
  void create_shouldThrow_whenPasswordNoSpecial() {
    UserRequest invalidRequest =
        new UserRequest("Dupont", "Marie", LocalDate.of(1995, 3, 10), "pw-nospecial@mail.com", "NoSpecialChar1Phrase", "NoSpecialChar1Phrase", null);

    doThrow(new UnprocessableEntityException("Password must contain at least one special character."))
        .when(userValidator)
        .validateUserCreation(invalidRequest);

    assertThatThrownBy(() -> authService.create(invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("special character");
  }

  @Test
  @DisplayName("create: should throw UnprocessableEntityException when passwords do not match")
  void create_shouldThrow_whenPasswordsDoNotMatch() {
    UserRequest mismatchedRequest =
        new UserRequest("Dupont", "Marie", LocalDate.of(1995, 3, 10), "mismatch@mail.com", "Str0ng!Passphrase", "Different1!Passphrase", null);

    doThrow(new UnprocessableEntityException("Passwords do not match."))
        .when(userValidator)
        .validateUserCreation(mismatchedRequest);

    assertThatThrownBy(() -> authService.create(mismatchedRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("Passwords do not match");
  }

  @Test
  @DisplayName("create: should throw UnprocessableEntityException when phone has invalid format")
  void create_shouldThrow_whenPhoneInvalidFormat() {
    UserRequest invalidRequest =
        new UserRequest("Dupont", "Marie", LocalDate.of(1995, 3, 10), "phone-invalid@mail.com", "Str0ng!Passphrase", "Str0ng!Passphrase", "not-a-phone");

    doThrow(new UnprocessableEntityException("Invalid phone format: 'not-a-phone'"))
        .when(userValidator)
        .validateUserCreation(invalidRequest);

    assertThatThrownBy(() -> authService.create(invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("Invalid phone format");
  }
}
