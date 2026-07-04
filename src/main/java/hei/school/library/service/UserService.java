package hei.school.library.service;

import hei.school.library.config.ResourcesAccessRules;
import hei.school.library.dto.*;
import hei.school.library.entity.User;
import hei.school.library.exception.ConflictException;
import hei.school.library.exception.ForbiddenException;
import hei.school.library.exception.InternalServerErrorException;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.UserMapper;
import hei.school.library.repository.dao.UserRepository;
import hei.school.library.validator.DataValidator;
import java.util.UUID;

import hei.school.library.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final DataValidator dataValidator;
  private final ResourcesAccessRules resourcesAccessRules;
  private final UserValidator userValidator;

  @Transactional(readOnly = true)
  public PageResponse<UserResponse> findAll(String search, int page, int size) {
    dataValidator.validateString("search", search);
    PageRequest pageable = PageRequest.of(page - 1, size);

    return (search == null || search.isBlank())
        ? userMapper.toPageResponse(userRepository.findAll(pageable), page, size)
        : userMapper.toPageResponse(userRepository.findBySearch(search, pageable), page, size);
  }

  @Transactional(readOnly = true)
  public UserResponse findById(UUID id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException(String.format("User %s not found", id)));

    if (resourcesAccessRules.grantAccessFor(user)) {
      return userMapper.toResponse(user);
    }
    throw new ForbiddenException(String.format("Cannot read user %s", id));
  }

  @Transactional
  public UserResponse update(UUID id, UserUpdateRequest request) {
    userValidator.validateUserPatch(request);

    User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("User %s not found", id)));

    if(resourcesAccessRules.grantAccessFor(user)) {
      return userRepository
              .patch(
                      id,
                      request.getLastName(),
                      request.getFirstName(),
                      request.getBirthDate(),
                      request.getPhone())
              .map(userMapper::toResponse)
              .orElseThrow(() -> new InternalServerErrorException("An error Occurred during the update, please try again later"));
    }
    else {
      throw new ForbiddenException(String.format("Cannot update user %s", id));
    }
  }

  @Transactional
  public void delete(UUID id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException(String.format("User %s not found", id)));

    if (resourcesAccessRules.grantAccessFor(user)) {
      userRepository.delete(user);
    } else {
      throw new ForbiddenException(String.format("Cannot delete user %s", id));
    }
  }
}
