package hei.school.library.service.user;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import hei.school.library.dto.PageResponse;
import hei.school.library.dto.UserResponse;
import hei.school.library.entity.User;
import hei.school.library.exception.UnprocessableEntityException;
import hei.school.library.mapper.PaginationMapper;
import hei.school.library.mapper.UserMapper;
import hei.school.library.repository.dao.UserRepository;
import hei.school.library.service.UserService;
import hei.school.library.validator.DataValidator;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class GetUsersServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private DataValidator dataValidator;
  @Mock private PasswordEncoder passwordEncoder;
  private UserService userService;
  private User user;

  @BeforeEach
  void setUp() {
    userService =
        new UserService(userRepository, new UserMapper(new PaginationMapper()), dataValidator, passwordEncoder);

    user =
        new User(
            UUID.randomUUID(),
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
  @DisplayName("findAll: should return page of users when search is null")
  void findAll_shouldReturnPage_whenSearchNull() {
    Page<User> page = new PageImpl<>(List.of(user));
    when(userRepository.findAll(any(Pageable.class))).thenReturn(page);

    PageResponse<UserResponse> result = userService.findAll(null, 1, 20);

    assertThat(result.getData()).hasSize(1);
    assertThat(result.getData().getFirst().getLastName()).isEqualTo("Dupont");
    assertThat(result.getPagination().getTotal()).isEqualTo(1);
    assertThat(result.getPagination().getPage()).isEqualTo(1);
    assertThat(result.getPagination().getSize()).isEqualTo(20);
    verify(userRepository).findAll(any(Pageable.class));
  }

  @Test
  @DisplayName("findAll: should search by keyword")
  void findAll_shouldSearch_whenKeywordGiven() {
    Page<User> page = new PageImpl<>(List.of(user));
    when(userRepository.findBySearch(any(), any(Pageable.class))).thenReturn(page);

    PageResponse<UserResponse> result = userService.findAll("Dupont", 1, 20);

    assertThat(result.getData()).hasSize(1);
    assertThat(result.getData().getFirst().getLastName()).isEqualTo("Dupont");
    verify(userRepository).findBySearch("Dupont", PageRequest.of(0, 20));
  }

  @Test
  @DisplayName("findAll: should not expose password in response")
  void findAll_shouldNotExposePassword() {
    Page<User> page = new PageImpl<>(List.of(user));
    when(userRepository.findAll(any(Pageable.class))).thenReturn(page);

    PageResponse<UserResponse> result = userService.findAll(null, 1, 20);

    assertThat(result.getData().getFirst()).isInstanceOf(UserResponse.class);
  }

  @Test
  @DisplayName(
      "findAll: should throw UnprocessableEntityException when search contains invalid characters")
  void findAll_shouldThrow_whenSearchInvalid() {
    String invalidSearch = "user!</>";

    doThrow(
            new UnprocessableEntityException(
                "Field 'search' contains invalid characters. Only letters (a-z, A-Z), digits (0-9),"
                    + " and @ ' . - _ are allowed."))
        .when(dataValidator)
        .validateString("search", invalidSearch);

    assertThatThrownBy(() -> userService.findAll(invalidSearch, 1, 20))
        .isInstanceOf(UnprocessableEntityException.class);
  }
}
