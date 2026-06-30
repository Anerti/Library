package hei.school.library.controller.user;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import hei.school.library.config.JwtTokenProvider;
import hei.school.library.dto.PageResponse;
import hei.school.library.dto.UserResponse;
import hei.school.library.dto.UserUpdateRequest;
import hei.school.library.endpoint.rest.controller.UserController;
import hei.school.library.exception.ForbiddenException;
import hei.school.library.exception.GlobalExceptionHandler;
import hei.school.library.exception.NotFoundException;
import hei.school.library.service.UserService;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({UserController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private UserService userService;
  @MockBean private JwtTokenProvider jwtTokenProvider;

  private UUID userId;
  private UserResponse userResponse;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();

    userResponse =
        UserResponse.builder()
            .id(userId)
            .lastName("Randria")
            .firstName("Faly")
            .birthDate(LocalDate.of(1995, 8, 12))
            .email("faly.randria@email.com")
            .phone("+261 32 11 234 56")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
  }

  @Test
  void should_get_all_users_with_pagination() throws Exception {
    String search = "Randria";
    PageResponse<UserResponse> mockPageResponse = new PageResponse<>();

    when(userService.findAll(search, 1, 20)).thenReturn(mockPageResponse);

    mockMvc
        .perform(get("/users").param("search", search).param("page", "1").param("size", "20"))
        .andExpect(status().isOk());

    verify(userService).findAll(search, 1, 20);
  }

  @Test
  void should_use_default_pagination_when_not_provided() throws Exception {
    PageResponse<UserResponse> mockPageResponse = new PageResponse<>();

    when(userService.findAll(null, 1, 20)).thenReturn(mockPageResponse);

    mockMvc.perform(get("/users")).andExpect(status().isOk());

    verify(userService).findAll(null, 1, 20);
  }

  @Test
  void should_get_user_by_id() throws Exception {
    when(userService.findById(userId)).thenReturn(userResponse);

    mockMvc
        .perform(get("/users/{id}", userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.lastName").value("Randria"))
        .andExpect(jsonPath("$.firstName").value("Faly"))
        .andExpect(jsonPath("$.email").value("faly.randria@email.com"));
  }

  @Test
  void should_return_not_found_when_user_does_not_exist() throws Exception {
    when(userService.findById(userId))
        .thenThrow(new NotFoundException("User " + userId + " not found"));

    mockMvc.perform(get("/users/{id}", userId)).andExpect(status().isNotFound());
  }

  @Test
  void should_return_forbidden_when_accessing_another_user_by_id() throws Exception {
    when(userService.findById(userId))
        .thenThrow(new ForbiddenException("Cannot read user " + userId));

    mockMvc.perform(get("/users/{id}", userId)).andExpect(status().isForbidden());
  }

  @Test
  void should_update_user() throws Exception {
    UserUpdateRequest request = new UserUpdateRequest(null, "Faly Updated", null, null, null);

    UserResponse updatedResponse =
        UserResponse.builder()
            .id(userId)
            .lastName("Randria")
            .firstName("Faly Updated")
            .birthDate(LocalDate.of(1995, 8, 12))
            .email("faly.randria@email.com")
            .phone("+261 32 11 234 56")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    when(userService.update(eq(userId), any(UserUpdateRequest.class))).thenReturn(updatedResponse);

    mockMvc
        .perform(
            patch("/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName").value("Faly Updated"));
  }

  @Test
  void should_return_not_found_when_user_does_not_exist_on_update() throws Exception {
    UserUpdateRequest request = new UserUpdateRequest(null, "Faly Updated", null, null, null);

    when(userService.update(eq(userId), any(UserUpdateRequest.class)))
        .thenThrow(new NotFoundException("User " + userId + " not found"));

    mockMvc
        .perform(
            patch("/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  void should_delete_user() throws Exception {
    doNothing().when(userService).delete(userId);

    mockMvc.perform(delete("/users/{id}", userId)).andExpect(status().isNoContent());

    verify(userService).delete(userId);
  }

  @Test
  void should_return_not_found_when_user_does_not_exist_on_delete() throws Exception {
    doThrow(new NotFoundException("User " + userId + " not found"))
        .when(userService)
        .delete(userId);

    mockMvc.perform(delete("/users/{id}", userId)).andExpect(status().isNotFound());
  }

  @Test
  void should_return_forbidden_when_deleting_another_user() throws Exception {
    doThrow(new ForbiddenException("Cannot delete another user")).when(userService).delete(userId);

    mockMvc.perform(delete("/users/{id}", userId)).andExpect(status().isForbidden());
  }
}
