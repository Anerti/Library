package hei.school.library.service;

import hei.school.library.dto.PageResponse;
import hei.school.library.dto.UserResponse;
import hei.school.library.dto.UserUpdateRequest;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.UserMapper;
import hei.school.library.repository.dao.UserRepository;
import hei.school.library.validator.DataValidator;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final DataValidator dataValidator;
  private final PasswordEncoder passwordEncoder;

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
    return userRepository
        .findById(id)
        .map(userMapper::toResponse)
        .orElseThrow(() -> new NotFoundException("User " + id + " not found"));
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

  @Transactional
  public void delete(UUID id) {
    userRepository
        .delete(id)
        .orElseThrow(() -> new NotFoundException(String.format("User %s not found", id)));
  }
}
