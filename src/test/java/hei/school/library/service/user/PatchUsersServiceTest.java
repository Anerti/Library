package hei.school.library.service.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hei.school.library.config.ResourcesAccessRules;
import hei.school.library.dto.UserResponse;
import hei.school.library.dto.UserUpdateRequest;
import hei.school.library.entity.User;
import hei.school.library.entity.enums.Role;
import hei.school.library.exception.ForbiddenException;
import hei.school.library.exception.NotFoundException;
import hei.school.library.exception.UnprocessableEntityException;
import hei.school.library.mapper.PaginationMapper;
import hei.school.library.mapper.UserMapper;
import hei.school.library.repository.dao.UserRepository;
import hei.school.library.service.UserService;
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

@ExtendWith(MockitoExtension.class)
class PatchUsersServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private ResourcesAccessRules resourcesAccessRules;
  @Mock private UserValidator userValidator;

  private UserService userService;

  private UUID existingId;
  private Instant now;
  private User existingUser;

  @BeforeEach
  void setUp() {
    userService =
        new UserService(
            userRepository,
            new UserMapper(new PaginationMapper()),
            new DataValidator(),
            resourcesAccessRules,
            userValidator);

    existingId = UUID.randomUUID();
    now = Instant.now();
    existingUser =
        new User(
            existingId,
            "Dupont",
            "Marie",
            LocalDate.of(1995, 3, 10),
            "marie@mail.com",
            "secret",
            "+261****4567",
            Role.CUSTOMER,
            now,
            now);
  }

  @Test
  @DisplayName("update: should update firstName only and return DTO")
  void update_shouldUpdateFirstNameOnly() {
    UserUpdateRequest request = new UserUpdateRequest(null, "Marie Claire", null, null);

    User updated =
        new User(
            existingId,
            "Dupont",
            "Marie Claire",
            LocalDate.of(1995, 3, 10),
            "marie@mail.com",
            "secret",
            "+261****4567",
            Role.CUSTOMER,
            now,
            now);

    when(userRepository.findById(existingId)).thenReturn(Optional.of(existingUser));
    when(resourcesAccessRules.grantAccessFor(existingUser)).thenReturn(true);
    when(userRepository.patch(
            eq(existingId), isNull(), eq("Marie Claire"), isNull(), isNull()))
        .thenReturn(Optional.of(updated));

    UserResponse result = userService.update(existingId, request);

    assertThat(result.getFirstName()).isEqualTo("Marie Claire");
    assertThat(result.getLastName()).isEqualTo("Dupont");
    assertThat(result.getEmail()).isEqualTo("marie@mail.com");
    verify(userRepository)
        .patch(eq(existingId), isNull(), eq("Marie Claire"), isNull(), isNull());
  }

  @Test
  @DisplayName("update: should update lastName only and return DTO")
  void update_shouldUpdateLastNameOnly() {
    UserUpdateRequest request = new UserUpdateRequest("Martin", null, null, null);

    User updated =
        new User(
            existingId,
            "Martin",
            "Marie",
            LocalDate.of(1995, 3, 10),
            "marie@mail.com",
            "secret",
            "+261****4567",
            Role.CUSTOMER,
            now,
            now);

    when(userRepository.findById(existingId)).thenReturn(Optional.of(existingUser));
    when(resourcesAccessRules.grantAccessFor(existingUser)).thenReturn(true);
    when(userRepository.patch(eq(existingId), eq("Martin"), isNull(), isNull(), isNull()))
        .thenReturn(Optional.of(updated));

    UserResponse result = userService.update(existingId, request);

    assertThat(result.getLastName()).isEqualTo("Martin");
    assertThat(result.getFirstName()).isEqualTo("Marie");
    assertThat(result.getEmail()).isEqualTo("marie@mail.com");
  }

  @Test
  @DisplayName("update: should update phone only and return DTO")
  void update_shouldUpdatePhoneOnly() {
    UserUpdateRequest request = new UserUpdateRequest(null, null, null, "+261 34 12 340 00");

    User updated =
        new User(
            existingId,
            "Dupont",
            "Marie",
            LocalDate.of(1995, 3, 10),
            "marie@mail.com",
            "secret",
            "+261 34 12 340 00",
            Role.CUSTOMER,
            now,
            now);

    when(userRepository.findById(existingId)).thenReturn(Optional.of(existingUser));
    when(resourcesAccessRules.grantAccessFor(existingUser)).thenReturn(true);
    when(userRepository.patch(
            eq(existingId), isNull(), isNull(), isNull(), eq("+261 34 12 340 00")))
        .thenReturn(Optional.of(updated));

    UserResponse result = userService.update(existingId, request);

    assertThat(result.getPhone()).isEqualTo("+261 34 12 340 00");
    assertThat(result.getLastName()).isEqualTo("Dupont");
  }

  @Test
  @DisplayName("update: should update birthDate only and return DTO")
  void update_shouldUpdateBirthDateOnly() {
    LocalDate newBirthDate = LocalDate.of(1990, 7, 15);
    UserUpdateRequest request = new UserUpdateRequest(null, null, newBirthDate, null);

    User updated =
        new User(
            existingId,
            "Dupont",
            "Marie",
            newBirthDate,
            "marie@mail.com",
            "secret",
            "+261****4567",
            Role.CUSTOMER,
            now,
            now);

    when(userRepository.findById(existingId)).thenReturn(Optional.of(existingUser));
    when(resourcesAccessRules.grantAccessFor(existingUser)).thenReturn(true);
    when(userRepository.patch(
            eq(existingId), isNull(), isNull(), eq(newBirthDate), isNull()))
        .thenReturn(Optional.of(updated));

    UserResponse result = userService.update(existingId, request);

    assertThat(result.getBirthDate()).isEqualTo(newBirthDate);
  }

  @Test
  @DisplayName("update: should update all fields at once")
  void update_shouldUpdateAllFields() {
    UserUpdateRequest request =
        new UserUpdateRequest(
            "Martin", "Jean", LocalDate.of(1988, 1, 1), "+261 34 12 349 99");

    User updated =
        new User(
            existingId,
            "Martin",
            "Jean",
            LocalDate.of(1988, 1, 1),
            "marie@mail.com",
            "secret",
            "+261 34 12 349 99",
            Role.CUSTOMER,
            now,
            now);

    when(userRepository.findById(existingId)).thenReturn(Optional.of(existingUser));
    when(resourcesAccessRules.grantAccessFor(existingUser)).thenReturn(true);
    when(userRepository.patch(
            eq(existingId),
            eq("Martin"),
            eq("Jean"),
            eq(LocalDate.of(1988, 1, 1)),
            eq("+261 34 12 349 99")))
        .thenReturn(Optional.of(updated));

    UserResponse result = userService.update(existingId, request);

    assertThat(result.getLastName()).isEqualTo("Martin");
    assertThat(result.getFirstName()).isEqualTo("Jean");
    assertThat(result.getBirthDate()).isEqualTo(LocalDate.of(1988, 1, 1));
    assertThat(result.getEmail()).isEqualTo("marie@mail.com");
    assertThat(result.getPhone()).isEqualTo("+261 34 12 349 99");
  }

  @Test
  @DisplayName("update: should throw NotFoundException when user not found")
  void update_shouldThrow_whenNotFound() {
    UUID unknownId = UUID.randomUUID();
    when(userRepository.findById(unknownId)).thenReturn(Optional.empty());

    assertThatThrownBy(
            () ->
                userService.update(
                    unknownId, new UserUpdateRequest("test", null, null, null)))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(unknownId.toString());

    verify(userRepository).findById(unknownId);
    verify(userRepository, never())
        .patch(any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("update: should throw ForbiddenException when user lacks access")
  void update_shouldThrow_whenForbidden() {
    UserUpdateRequest request = new UserUpdateRequest("Martin", null, null, null);

    when(userRepository.findById(existingId)).thenReturn(Optional.of(existingUser));
    when(resourcesAccessRules.grantAccessFor(existingUser)).thenReturn(false);

    assertThatThrownBy(() -> userService.update(existingId, request))
        .isInstanceOf(ForbiddenException.class)
        .hasMessageContaining(existingId.toString());

    verify(userRepository).findById(existingId);
    verify(userRepository, never())
        .patch(any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("update: should propagate UnprocessableEntityException when lastName is invalid")
  void update_shouldThrow_whenLastNameInvalid() {
    UserUpdateRequest request = new UserUpdateRequest("Dupont123", null, null, null);

    doThrow(new UnprocessableEntityException("lastName field contain forbidden characters."))
        .when(userValidator)
        .validateUserPatch(request);

    assertThatThrownBy(() -> userService.update(existingId, request))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("forbidden characters");
  }

  @Test
  @DisplayName("update: should propagate UnprocessableEntityException when phone is invalid")
  void update_shouldThrow_whenPhoneInvalid() {
    UserUpdateRequest request = new UserUpdateRequest(null, null, null, "not-a-phone");

    doThrow(new UnprocessableEntityException("Invalid phone format: 'not-a-phone'"))
        .when(userValidator)
        .validateUserPatch(request);

    assertThatThrownBy(() -> userService.update(existingId, request))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("Invalid phone format");
  }
}
