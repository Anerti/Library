package hei.school.library.service.user;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class DeleteUsersByIdServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;

  private UserService userService;

  private UUID existingId;
  private UUID unknownId;

  @BeforeEach
  void setUp() {
    userService =
        new UserService(
            userRepository, new UserMapper(new PaginationMapper()), new DataValidator(), passwordEncoder);

    existingId = UUID.randomUUID();
    unknownId = UUID.randomUUID();
  }

  @Test
  @DisplayName("delete: should delete when user exists")
  void delete_shouldDelete_whenExists() {
    when(userRepository.delete(existingId)).thenReturn(Optional.of(existingId));

    userService.delete(existingId);

    verify(userRepository).delete(existingId);
  }

  @Test
  @DisplayName("delete: should throw NotFoundException when absent")
  void delete_shouldThrow_whenNotFound() {
    when(userRepository.delete(unknownId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.delete(unknownId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(unknownId.toString());

    verify(userRepository).delete(unknownId);
  }
}
