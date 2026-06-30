package hei.school.library.service.user;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import hei.school.library.exception.ForbiddenException;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.PaginationMapper;
import hei.school.library.mapper.UserMapper;
import hei.school.library.repository.dao.UserRepository;
import hei.school.library.service.UserService;
import hei.school.library.validator.DataValidator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class DeleteUsersByIdServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private Authentication auth;

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
            new DataValidator());

    existingId = UUID.randomUUID();
    unknownId = UUID.randomUUID();
    otherId = UUID.randomUUID();
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("delete: should delete when authenticated user deletes own account")
  void delete_shouldDelete_whenOwnAccount() {
    doReturn(existingId.toString()).when(auth).getName();
    doReturn(List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))).when(auth).getAuthorities();
    SecurityContextHolder.getContext().setAuthentication(auth);

    when(userRepository.delete(existingId)).thenReturn(Optional.of(existingId));

    userService.delete(existingId);

    verify(userRepository).delete(existingId);
  }

  @Test
  @DisplayName("delete: should throw NotFoundException when user does not exist")
  void delete_shouldThrow_whenNotFound() {
    doReturn(unknownId.toString()).when(auth).getName();
    doReturn(List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))).when(auth).getAuthorities();
    SecurityContextHolder.getContext().setAuthentication(auth);

    when(userRepository.delete(unknownId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.delete(unknownId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(unknownId.toString());

    verify(userRepository).delete(unknownId);
  }

  @Test
  @DisplayName("delete: should throw ForbiddenException when deleting another user")
  void delete_shouldThrow_whenDeletingOtherUser() {
    doReturn(existingId.toString()).when(auth).getName();
    doReturn(List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))).when(auth).getAuthorities();
    SecurityContextHolder.getContext().setAuthentication(auth);

    assertThatThrownBy(() -> userService.delete(otherId))
        .isInstanceOf(ForbiddenException.class)
        .hasMessageContaining("Cannot delete user %s".formatted(otherId));

    verify(userRepository, never()).delete(any(UUID.class));
  }
}
