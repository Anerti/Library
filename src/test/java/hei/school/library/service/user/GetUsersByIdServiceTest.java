package hei.school.library.service.user;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import hei.school.library.dto.UserResponse;
import hei.school.library.entity.User;
import hei.school.library.entity.enums.Role;
import hei.school.library.exception.ForbiddenException;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.PaginationMapper;
import hei.school.library.mapper.UserMapper;
import hei.school.library.repository.dao.UserRepository;
import hei.school.library.service.UserService;
import hei.school.library.validator.DataValidator;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class GetUsersByIdServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private Authentication auth;

  private UserService userService;

  private UUID existingId;
  private UUID unknownId;
  private UUID otherId;
  private User user;

  @BeforeEach
  void setUp() {
    userService =
        new UserService(
            userRepository, new UserMapper(new PaginationMapper()), new DataValidator());

    existingId = UUID.randomUUID();
    unknownId = UUID.randomUUID();
    otherId = UUID.randomUUID();
    user =
        new User(
            existingId,
            "Dupont",
            "Marie",
            LocalDate.of(1995, 3, 10),
            "marie@mail.com",
            "secret",
            "+261****4567",
            Role.CUSTOMER,
            Instant.now(),
            Instant.now());
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("findById: should return user when CUSTOMER reads own account")
  void findById_shouldReturnUser() {
    doReturn(existingId.toString()).when(auth).getName();
    doReturn(List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))).when(auth).getAuthorities();
    SecurityContextHolder.getContext().setAuthentication(auth);

    when(userRepository.findById(existingId)).thenReturn(Optional.of(user));

    UserResponse result = userService.findById(existingId);

    assertThat(result.getLastName()).isEqualTo("Dupont");
    assertThat(result.getEmail()).isEqualTo("marie@mail.com");
    verify(userRepository).findById(existingId);
  }

  @Test
  @DisplayName("findById: should return user when ADMIN reads a CUSTOMER")
  void findById_shouldReturnUser_whenAdminReadsCustomer() {
    doReturn(existingId.toString()).when(auth).getName();
    doReturn(List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))).when(auth).getAuthorities();
    SecurityContextHolder.getContext().setAuthentication(auth);

    when(userRepository.findById(otherId)).thenReturn(Optional.of(user));

    UserResponse result = userService.findById(otherId);

    assertThat(result.getLastName()).isEqualTo("Dupont");
    assertThat(result.getEmail()).isEqualTo("marie@mail.com");
    verify(userRepository).findById(otherId);
  }

  @Test
  @DisplayName("findById: should throw NotFoundException when own account does not exist")
  void findById_shouldThrow_whenNotFound() {
    doReturn(unknownId.toString()).when(auth).getName();
    doReturn(List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))).when(auth).getAuthorities();
    SecurityContextHolder.getContext().setAuthentication(auth);

    when(userRepository.findById(unknownId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.findById(unknownId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(unknownId.toString());

    verify(userRepository).findById(unknownId);
  }

  @Test
  @DisplayName("findById: should throw ForbiddenException when CUSTOMER accesses another user")
  void findById_shouldThrow_whenAccessingOtherUser() {
    doReturn(existingId.toString()).when(auth).getName();
    doReturn(List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))).when(auth).getAuthorities();
    SecurityContextHolder.getContext().setAuthentication(auth);

    assertThatThrownBy(() -> userService.findById(otherId))
        .isInstanceOf(ForbiddenException.class)
        .hasMessageContaining("Cannot read user %s".formatted(otherId));

    verify(userRepository, never()).findById(any(UUID.class));
  }

  @Test
  @DisplayName("findById: should throw NotFoundException when ADMIN reads non-existent user")
  void findById_shouldThrow_whenAdminReadsNonExistent() {
    doReturn(existingId.toString()).when(auth).getName();
    doReturn(List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))).when(auth).getAuthorities();
    SecurityContextHolder.getContext().setAuthentication(auth);

    when(userRepository.findById(unknownId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.findById(unknownId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(unknownId.toString());

    verify(userRepository).findById(unknownId);
  }
}
