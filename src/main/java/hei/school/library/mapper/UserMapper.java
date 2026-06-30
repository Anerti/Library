package hei.school.library.mapper;

import hei.school.library.dto.AuthResponse.AuthUser;
import hei.school.library.dto.PageResponse;
import hei.school.library.dto.UserResponse;
import hei.school.library.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

  private final PaginationMapper paginationMapper;

  public UserResponse toResponse(User user) {
    return UserResponse.builder()
        .id(user.getId())
        .lastName(user.getLastName())
        .firstName(user.getFirstName())
        .birthDate(user.getBirthDate())
        .email(user.getEmail())
        .phone(user.getPhone())
        .role(user.getRole())
        .createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt())
        .build();
  }

  public PageResponse<UserResponse> toPageResponse(Page<User> page, int pageNum, int pageSize) {
    return PageResponse.<UserResponse>builder()
        .data(
            page.getContent().stream().map(this::toResponse).toList().isEmpty()
                ? null
                : page.getContent().stream().map(this::toResponse).toList())
        .pagination(paginationMapper.toPaginationDto(page, pageNum, pageSize))
        .build();
  }

  public AuthUser toAuthUser(UserResponse user) {
    return AuthUser.builder()
        .email(user.getEmail())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .birthDate(user.getBirthDate())
        .phone(user.getPhone())
        .build();
  }
}
