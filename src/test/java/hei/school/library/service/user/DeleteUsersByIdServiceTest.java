package hei.school.library.service.user;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import hei.school.library.config.ResourcesAccessRules;
import hei.school.library.exception.ForbiddenException;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.PaginationMapper;
import hei.school.library.mapper.UserMapper;
import hei.school.library.repository.dao.UserRepository;
import hei.school.library.service.UserService;
import hei.school.library.validator.DataValidator;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteUsersByIdServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private ResourcesAccessRules resourcesAccessRules;

  private UserService userService;

  private UUID existingId;
  private UUID unknownId;
  private UUID otherId;

  @BeforeEach
  void setUp() {
    userService =
        new UserService(
            userRepository,
            new UserMapper(new PaginationMapper()),
            new DataValidator(),
            resourcesAccessRules);

    existingId = UUID.randomUUID();
    unknownId = UUID.randomUUID();
    otherId = UUID.randomUUID();
  }

  @Test
  @DisplayName("delete: should delete when authenticated user deletes own account")
  void delete_shouldDelete_whenOwnAccount() {
    when(resourcesAccessRules.GrantAccessFor(existingId)).thenReturn(true);
    when(userRepository.delete(existingId)).thenReturn(Optional.of(existingId));

    userService.delete(existingId);

    verify(userRepository).delete(existingId);
  }

  @Test
  @DisplayName("delete: should throw NotFoundException when user does not exist")
  void delete_shouldThrow_whenNotFound() {
    when(resourcesAccessRules.GrantAccessFor(unknownId)).thenReturn(true);
    when(userRepository.delete(unknownId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.delete(unknownId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(unknownId.toString());

    verify(userRepository).delete(unknownId);
  }

  @Test
  @DisplayName("delete: should throw ForbiddenException when CUSTOMER deletes another user")
  void delete_shouldThrow_whenCustomerDeletesOtherUser() {
    when(resourcesAccessRules.GrantAccessFor(otherId)).thenReturn(false);

    assertThatThrownBy(() -> userService.delete(otherId))
        .isInstanceOf(ForbiddenException.class)
        .hasMessageContaining("Cannot delete user %s".formatted(otherId));

    verify(userRepository, never()).delete(any(UUID.class));
  }

  @Test
  @DisplayName("delete: should delete when ADMIN deletes another user")
  void delete_shouldDelete_whenAdminDeletesOtherUser() {
    when(resourcesAccessRules.GrantAccessFor(otherId)).thenReturn(true);
    when(userRepository.delete(otherId)).thenReturn(Optional.of(otherId));

    userService.delete(otherId);

    verify(userRepository).delete(otherId);
  }

  @Test
  @DisplayName("delete: should throw NotFoundException when ADMIN deletes non-existent user")
  void delete_shouldThrow_whenAdminDeletesNonExistent() {
    when(resourcesAccessRules.GrantAccessFor(unknownId)).thenReturn(true);
    when(userRepository.delete(unknownId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.delete(unknownId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(unknownId.toString());

    verify(userRepository).delete(unknownId);
  }
}
