package hei.school.library.service.sale;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import hei.school.library.dto.SaleRequest;
import hei.school.library.dto.SaleResponse;
import hei.school.library.dto.UserRequest;
import hei.school.library.dto.UserResponse;
import hei.school.library.entity.Library;
import hei.school.library.entity.Sale;
import hei.school.library.entity.User;
import hei.school.library.entity.enums.Role;
import hei.school.library.entity.enums.SaleStatus;
import hei.school.library.exception.NotFoundException;
import hei.school.library.exception.UnprocessableEntityException;
import hei.school.library.mapper.SaleMapper;
import hei.school.library.mapper.UserMapper;
import hei.school.library.repository.dao.LibraryRepository;
import hei.school.library.repository.dao.SaleRepository;
import hei.school.library.repository.dao.UserRepository;
import hei.school.library.service.SaleService;
import hei.school.library.validator.SaleValidator;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostSalesServiceTest {

  @Mock private SaleRepository saleRepository;
  @Mock private UserRepository userRepository;
  @Mock private LibraryRepository libraryRepository;
  @Mock private SaleValidator saleValidator;
  @Mock private UserMapper userMapper;
  private SaleMapper saleMapper;
  private SaleService saleService;

  private UUID libraryId;
  private UUID userId;
  private UUID saleId;
  private Library library;
  private User user;
  private Sale sale;
  private SaleRequest validRequest;
  private UserResponse userResponse;

  @BeforeEach
  void setUp() {
    saleMapper = new SaleMapper();
    saleService =
        new SaleService(
            saleRepository,
            userRepository,
            libraryRepository,
            userMapper,
            saleMapper,
            saleValidator);

    libraryId = UUID.randomUUID();
    userId = UUID.randomUUID();
    saleId = UUID.randomUUID();

    library = new Library(libraryId, "Lib A", "+261****4567", "lib@mail.com", "Antananarivo");
    user =
        new User(
            userId,
            "Dupont",
            "Marie",
            LocalDate.of(1995, 3, 10),
            "marie@mail.com",
            "secret",
            "+261****4567",
            Role.CUSTOMER,
            Instant.now(),
            Instant.now());
    sale =
        new Sale(saleId, Instant.now(), SaleStatus.BOOKED, userId, libraryId, null, Instant.now());

    userResponse =
        new UserResponse(
            userId,
            "Dupont",
            "Marie",
            LocalDate.of(1995, 3, 10),
            "marie@mail.com",
            "+261****4567",
            Role.CUSTOMER,
            Instant.now(),
            Instant.now());

    UserRequest userRequest =
        new UserRequest(
            "Dupont", "Marie", LocalDate.of(1995, 3, 10), "marie@mail.com", null, "+261****4567");
    validRequest = new SaleRequest(null, userRequest, null);
  }

  @Test
  @DisplayName("create: should save and return DTO")
  void create_shouldSaveAndReturnDto() {
    when(libraryRepository.findById(libraryId)).thenReturn(Optional.of(library));
    when(userRepository.findByEmail("marie@mail.com")).thenReturn(Optional.of(user));
    when(saleRepository.create(any(), any(), any(), any(), any())).thenReturn(Optional.of(sale));
    when(userMapper.toResponse(user)).thenReturn(userResponse);

    SaleResponse result = saleService.create(libraryId, validRequest);

    assertThat(result.getStatus()).isEqualTo(SaleStatus.BOOKED);
    assertThat(result.getUser().getEmail()).isEqualTo("marie@mail.com");
    verify(saleValidator).validateCreate(validRequest);
  }

  @Test
  @DisplayName("create: should create new user when not found by email")
  void create_shouldCreateUser_whenNotFound() {
    when(libraryRepository.findById(libraryId)).thenReturn(Optional.of(library));
    when(userRepository.findByEmail("marie@mail.com")).thenReturn(Optional.empty());
    when(userRepository.create(any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(Optional.of(user));
    when(saleRepository.create(any(), any(), any(), any(), any())).thenReturn(Optional.of(sale));
    when(userMapper.toResponse(user)).thenReturn(userResponse);

    SaleResponse result = saleService.create(libraryId, validRequest);

    assertThat(result.getUser().getEmail()).isEqualTo("marie@mail.com");
    verify(userRepository).create(any(), any(), any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("create: should throw NotFoundException when library not found")
  void create_shouldThrow_whenLibraryNotFound() {
    UUID unknownLibraryId = UUID.randomUUID();
    when(libraryRepository.findById(unknownLibraryId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> saleService.create(unknownLibraryId, validRequest))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  @DisplayName("create: should throw UnprocessableEntityException when user is null")
  void create_shouldThrow_whenUserNull() {
    SaleRequest invalidRequest = new SaleRequest(null, null, null);

    doThrow(new UnprocessableEntityException("user is required."))
        .when(saleValidator)
        .validateCreate(invalidRequest);

    assertThatThrownBy(() -> saleService.create(libraryId, invalidRequest))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("user is required.");
  }
}
