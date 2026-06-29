package hei.school.library.service.user;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import hei.school.library.dto.UserResponse;
import hei.school.library.entity.User;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.PaginationMapper;
import hei.school.library.mapper.UserMapper;
import hei.school.library.repository.dao.UserRepository;
import hei.school.library.service.UserService;
import hei.school.library.validator.DataValidator;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class GetUsersByIdServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;

  private UserService userService;

  private UUID existingId;
  private UUID unknownId;
  private User user;

  @BeforeEach
  void setUp() {
    userService =
        new UserService(
            userRepository, new UserMapper(new PaginationMapper()), new DataValidator(), passwordEncoder);

    existingId = UUID.randomUUID();
    unknownId = UUID.randomUUID();
    user =
        new User(
            existingId,
            "Dupont",
            "Marie",
            LocalDate.of(1995, 3, 10),
            "marie@mail.com",
            "secret",
            "+261****4567",
            null,
            Instant.now(),
            Instant.now());
  }

  @Test
  @DisplayName("findById: should return user when found")
  void findById_shouldReturnUser() {
    when(userRepository.findById(existingId)).thenReturn(Optional.of(user));

    UserResponse result = userService.findById(existingId);

    assertThat(result.getLastName()).isEqualTo("Dupont");
    assertThat(result.getEmail()).isEqualTo("marie@mail.com");
    verify(userRepository).findById(existingId);
  }

  @Test
  @DisplayName("findById: should throw NotFoundException when absent")
  void findById_shouldThrow_whenNotFound() {
    when(userRepository.findById(unknownId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.findById(unknownId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(unknownId.toString());
  }
}
