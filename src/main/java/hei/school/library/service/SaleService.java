package hei.school.library.service;

import hei.school.library.dto.*;
import hei.school.library.entity.Library;
import hei.school.library.entity.Sale;
import hei.school.library.entity.User;
import hei.school.library.entity.enums.Role;
import hei.school.library.entity.enums.SaleStatus;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.SaleMapper;
import hei.school.library.mapper.UserMapper;
import hei.school.library.repository.dao.LibraryRepository;
import hei.school.library.repository.dao.AuthRepository;
import hei.school.library.repository.dao.SaleRepository;
import hei.school.library.repository.dao.UserRepository;
import hei.school.library.validator.SaleValidator;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SaleService {

  private final SaleRepository saleRepository;
  private final AuthRepository authRepository;
  private final UserRepository userRepository;
  private final LibraryRepository libraryRepository;
  private final UserMapper userMapper;
  private final SaleMapper saleMapper;
  private final SaleValidator saleValidator;

  @Transactional(readOnly = true)
  public PageResponse<SaleResponse> findAll(
      UUID libraryId,
      SaleStatus status,
      UUID customerId,
      Instant from,
      Instant to,
      int page,
      int size) {

    libraryRepository
        .findById(libraryId)
        .orElseThrow(() -> new NotFoundException("Library " + libraryId + " not found"));

    PageRequest pageable = PageRequest.of(page - 1, size);
    String statusStr = status != null ? status.name() : null;

    Page<Sale> salePage =
        saleRepository.findByLibraryId(libraryId, statusStr, customerId, from, to, pageable);

    List<SaleResponse> responses =
        salePage.getContent().stream()
            .map(
                sale -> {
                  User user =
                      userRepository
                          .findById(sale.getUserId())
                          .orElseThrow(
                              () ->
                                  new NotFoundException("User " + sale.getUserId() + " not found"));
                  Library library =
                      libraryRepository
                          .findById(sale.getLibraryId())
                          .orElseThrow(
                              () ->
                                  new NotFoundException(
                                      "Library " + sale.getLibraryId() + " not found"));

                  UserResponse userResponse = userMapper.toResponse(user);
                  LibraryResponse libraryResponse =
                      new LibraryResponse(
                          library.getId(),
                          library.getName(),
                          library.getPhone(),
                          library.getEmail(),
                          library.getAddress());

                  return saleMapper.toResponse(sale, userResponse, libraryResponse);
                })
            .toList();

    Page<SaleResponse> responsePage =
        new PageImpl<>(responses, pageable, salePage.getTotalElements());

    return saleMapper.toPageResponse(responsePage, page, size);
  }

  @Transactional(readOnly = true)
  public SaleResponse findById(UUID libraryId, UUID saleId) {
    libraryRepository
        .findById(libraryId)
        .orElseThrow(() -> new NotFoundException("Library " + libraryId + " not found"));

    Sale sale =
        saleRepository
            .findById(saleId)
            .orElseThrow(() -> new NotFoundException("Sale " + saleId + " not found"));

    User user =
        userRepository
            .findById(sale.getUserId())
            .orElseThrow(() -> new NotFoundException("User " + sale.getUserId() + " not found"));

    Library library =
        libraryRepository
            .findById(sale.getLibraryId())
            .orElseThrow(
                () -> new NotFoundException("Library " + sale.getLibraryId() + " not found"));

    UserResponse userResponse = userMapper.toResponse(user);
    LibraryResponse libraryResponse =
        new LibraryResponse(
            library.getId(),
            library.getName(),
            library.getPhone(),
            library.getEmail(),
            library.getAddress());

    return saleMapper.toResponse(sale, userResponse, libraryResponse);
  }

  @Transactional
  public SaleResponse create(UUID libraryId, SaleRequest request) {
    saleValidator.validateCreate(request);

    Library library =
        libraryRepository
            .findById(libraryId)
            .orElseThrow(() -> new NotFoundException("Library " + libraryId + " not found"));

    UserRequest userRequest = request.getUser();
    User user =
        userRepository
            .findByEmail(userRequest.getEmail())
            .orElseGet(
                () ->
                    authRepository
                        .create(
                            userRequest.getLastName(),
                            userRequest.getFirstName(),
                            userRequest.getBirthDate(),
                            userRequest.getEmail(),
                            userRequest.getPassword(),
                            userRequest.getPhone(),
                            Role.CUSTOMER.name())
                        .orElseThrow(
                            () ->
                                new NotFoundException(
                                    "Failed to create user with email " + userRequest.getEmail())));

    Instant saleDate = request.getSaleDate() != null ? request.getSaleDate() : Instant.now();
    SaleStatus status = request.getStatus() != null ? request.getStatus() : SaleStatus.BOOKED;
    Instant expirationDate = saleDate.plus(14, java.time.temporal.ChronoUnit.DAYS);

    Sale sale =
        saleRepository
            .create(saleDate, status.name(), user.getId(), libraryId, expirationDate)
            .orElseThrow(() -> new NotFoundException("Failed to create sale"));

    UserResponse userResponse = userMapper.toResponse(user);
    LibraryResponse libraryResponse =
        new LibraryResponse(
            library.getId(),
            library.getName(),
            library.getPhone(),
            library.getEmail(),
            library.getAddress());

    return saleMapper.toResponse(sale, userResponse, libraryResponse);
  }

  private SaleResponse buildSaleResponse(Sale sale) {
    User user =
        userRepository
            .findById(sale.getUserId())
            .orElseThrow(() -> new NotFoundException("User " + sale.getUserId() + " not found"));
    Library library =
        libraryRepository
            .findById(sale.getLibraryId())
            .orElseThrow(
                () -> new NotFoundException("Library " + sale.getLibraryId() + " not found"));

    UserResponse userResponse = userMapper.toResponse(user);
    LibraryResponse libraryResponse =
        new LibraryResponse(
            library.getId(),
            library.getName(),
            library.getPhone(),
            library.getEmail(),
            library.getAddress());

    return saleMapper.toResponse(sale, userResponse, libraryResponse);
  }

  @Transactional
  public SaleResponse update(UUID libraryId, UUID saleId, SaleUpdateRequest request) {
    saleValidator.validateUpdate(request);

    libraryRepository
        .findById(libraryId)
        .orElseThrow(() -> new NotFoundException("Library " + libraryId + " not found"));

    Sale sale =
        saleRepository
            .findById(saleId)
            .orElseThrow(() -> new NotFoundException("Sale " + saleId + " not found"));

    if (request.getStatus() != null) sale.setStatus(request.getStatus());
    if (request.getSaleDate() != null) sale.setSaleDate(request.getSaleDate());

    Sale updated = saleRepository.save(sale);
    return buildSaleResponse(updated);
  }
}
