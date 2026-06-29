package hei.school.library.controller.auth;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import hei.school.library.config.JwtTokenProvider;
import hei.school.library.dto.AuthResponse.AuthUser;
import hei.school.library.dto.LoginRequest;
import hei.school.library.dto.RegisterRequest;
import hei.school.library.dto.UserResponse;
import hei.school.library.endpoint.rest.controller.AuthController;
import hei.school.library.entity.enums.Role;
import hei.school.library.exception.ConflictException;
import hei.school.library.exception.GlobalExceptionHandler;
import hei.school.library.exception.UnauthorizedException;
import hei.school.library.exception.UnprocessableEntityException;
import hei.school.library.mapper.UserMapper;
import hei.school.library.service.AuthService;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({AuthController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private AuthService authService;

  @MockBean private JwtTokenProvider jwtTokenProvider;

  @MockBean private UserMapper userMapper;

  private RegisterRequest validRequest;
  private UserResponse userResponse;
  private AuthUser authUser;
  private String token;

  @BeforeEach
  void setUp() {
    UUID id = UUID.randomUUID();
    token = "test-jwt-token";

    userResponse =
        UserResponse.builder()
            .id(id)
            .lastName("Dupont")
            .firstName("Marie")
            .birthDate(LocalDate.of(1995, 3, 10))
            .email("marie.register@mail.com")
            .phone("+261 32 456 789")
            .role(Role.CUSTOMER)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    authUser =
        AuthUser.builder()
            .email("marie.register@mail.com")
            .firstName("Marie")
            .lastName("Dupont")
            .birthDate(LocalDate.of(1995, 3, 10))
            .phone("+261 32 456 789")
            .build();

    validRequest =
        new RegisterRequest(
            "Dupont",
            "Marie",
            LocalDate.of(1995, 3, 10),
            "marie.register@mail.com",
            "Str0ng!Passphrase",
            "Str0ng!Passphrase",
            "+261 32 456 789");
  }

  @Test
  @DisplayName("register: should return 201 with token and user")
  void register_shouldReturn201() throws Exception {
    when(authService.create(any(RegisterRequest.class))).thenReturn(userResponse);
    when(jwtTokenProvider.generateToken(userResponse.getId().toString(), "CUSTOMER"))
        .thenReturn(token);
    when(userMapper.toAuthUser(userResponse)).thenReturn(authUser);

    mockMvc
        .perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.token").value(token))
        .andExpect(jsonPath("$.user.email").value("marie.register@mail.com"))
        .andExpect(jsonPath("$.user.firstName").value("Marie"))
        .andExpect(jsonPath("$.user.lastName").value("Dupont"))
        .andExpect(jsonPath("$.user.birthDate").value("1995-03-10"))
        .andExpect(jsonPath("$.user.phone").value("+261 32 456 789"));
  }

  @Test
  @DisplayName("register: should return 409 when email already exists")
  void register_shouldReturn409_whenDuplicateEmail() throws Exception {
    when(authService.create(any(RegisterRequest.class)))
        .thenThrow(new ConflictException("User email marie.register@mail.com already used."));

    mockMvc
        .perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(jsonPath("$.error").value("CONFLICT"))
        .andExpect(jsonPath("$.message").value("User email marie.register@mail.com already used."));
  }

  @Test
  @DisplayName("register: should return 422 when validation fails")
  void register_shouldReturn422_whenValidationFails() throws Exception {
    when(authService.create(any(RegisterRequest.class)))
        .thenThrow(
            new UnprocessableEntityException("lastName is required and cannot be blank."));

    RegisterRequest invalidRequest =
        new RegisterRequest(
            null,
            "Marie",
            LocalDate.of(1995, 3, 10),
            "test@mail.com",
            "Str0ng!Passphrase",
            "Str0ng!Passphrase",
            null);

    mockMvc
        .perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.status").value(422))
        .andExpect(jsonPath("$.error").value("UNPROCESSABLE_ENTITY"))
        .andExpect(jsonPath("$.message").value("lastName is required and cannot be blank."));
  }

  @Test
  @DisplayName("login: should return 200 with token and user")
  void login_shouldReturn200() throws Exception {
    when(authService.login(any(LoginRequest.class))).thenReturn(userResponse);
    when(jwtTokenProvider.generateToken(userResponse.getId().toString(), "CUSTOMER"))
        .thenReturn(token);
    when(userMapper.toAuthUser(userResponse)).thenReturn(authUser);

    LoginRequest loginRequest = new LoginRequest("marie.register@mail.com", "Str0ng!Passphrase");

    mockMvc
        .perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value(token))
        .andExpect(jsonPath("$.user.email").value("marie.register@mail.com"))
        .andExpect(jsonPath("$.user.firstName").value("Marie"))
        .andExpect(jsonPath("$.user.lastName").value("Dupont"))
        .andExpect(jsonPath("$.user.birthDate").value("1995-03-10"))
        .andExpect(jsonPath("$.user.phone").value("+261 32 456 789"));
  }

  @Test
  @DisplayName("login: should return 401 when credentials are invalid")
  void login_shouldReturn401_whenInvalidCredentials() throws Exception {
    when(authService.login(any(LoginRequest.class)))
        .thenThrow(new UnauthorizedException("Invalid credentials."));

    LoginRequest loginRequest =
        new LoginRequest("wrong@mail.com", "WrongPassword1");

    mockMvc
        .perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status").value(401))
        .andExpect(jsonPath("$.error").value("UNAUTHORIZED"))
        .andExpect(jsonPath("$.message").value("Invalid credentials."));
  }

  @Test
  @DisplayName("login: should return 422 when email is missing")
  void login_shouldReturn422_whenEmailMissing() throws Exception {
    when(authService.login(any(LoginRequest.class)))
        .thenThrow(new UnprocessableEntityException("email is required and cannot be blank."));

    LoginRequest loginRequest = new LoginRequest(null, "Str0ng!Passphrase");

    mockMvc
        .perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.status").value(422))
        .andExpect(jsonPath("$.error").value("UNPROCESSABLE_ENTITY"))
        .andExpect(jsonPath("$.message").value("email is required and cannot be blank."));
  }
}
