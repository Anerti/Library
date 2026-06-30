package hei.school.library.service;

import hei.school.library.dto.*;
import hei.school.library.exception.ForbiddenException;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.UserMapper;
import hei.school.library.repository.dao.UserRepository;
import hei.school.library.validator.DataValidator;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final DataValidator dataValidator;

  @Transactional(readOnly = true)
  public PageResponse<UserResponse> findAll(String search, int page, int size) {
    dataValidator.validateString("search", search);
    PageRequest pageable = PageRequest.of(page - 1, size);

    return (search == null || search.isBlank())
        ? userMapper.toPageResponse(userRepository.findAll(pageable), page, size)
        : userMapper.toPageResponse(userRepository.findBySearch(search, pageable), page, size);
  }

  private boolean resourcesAccessGrantedForReading(UUID requestedResourceId) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String authenticatedUserId = auth.getName();
    String role = auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

    return (authenticatedUserId.equals(requestedResourceId.toString())) || role.equals("ADMIN");
  }

  @Transactional(readOnly = true)
  public UserResponse findById(UUID id) {

    if (resourcesAccessGrantedForReading(id)) {
      return userRepository
          .findById(id)
          .map(userMapper::toResponse)
          .orElseThrow(() -> new NotFoundException(String.format("User %s not found", id)));
    } else {
      throw new ForbiddenException(String.format("Cannot read user %s", id));
    }
  }

  @Transactional
  public UserResponse update(UUID id, UserUpdateRequest request) {
    dataValidator.validateUserUpdate(request);
    dataValidator.validateUserPatchFields(request);

    return userRepository
        .patch(
            id,
            request.getLastName(),
            request.getFirstName(),
            request.getBirthDate(),
            request.getEmail(),
            request.getPhone())
        .map(userMapper::toResponse)
        .orElseThrow(() -> new NotFoundException("User " + id + " not found"));
  }

  private boolean resourcesAccessGranted(UUID requestedResourceId) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String authenticatedUserId = auth.getName();
    String role = auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

    return role.equals("ADMIN") && authenticatedUserId.equals(requestedResourceId.toString())
        || role.equals("CUSTOMER") && authenticatedUserId.equals(requestedResourceId.toString());
  }

  @Transactional
  public void delete(UUID id) {

    if (resourcesAccessGranted(id)) {
      userRepository
          .delete(id)
          .orElseThrow(() -> new NotFoundException(String.format("User %s not found", id)));
    } else {
      throw new ForbiddenException(String.format("Cannot delete user %s", id));
    }
  }
}
