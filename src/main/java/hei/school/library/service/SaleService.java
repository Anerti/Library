package hei.school.library.service;

import hei.school.library.dto.CustomerResponse;
import hei.school.library.dto.LibraryResponse;
import hei.school.library.dto.PageResponse;
import hei.school.library.dto.SaleResponse;
import hei.school.library.entity.Customer;
import hei.school.library.entity.Library;
import hei.school.library.entity.Sale;
import hei.school.library.entity.enums.SaleStatus;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.CustomerMapper;
import hei.school.library.mapper.SaleMapper;
import hei.school.library.repository.dao.CustomerRepository;
import hei.school.library.repository.dao.LibraryRepository;
import hei.school.library.repository.dao.SaleRepository;
import java.time.Instant;
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
  private final CustomerRepository customerRepository;
  private final LibraryRepository libraryRepository;
  private final CustomerMapper customerMapper;
  private final SaleMapper saleMapper;

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

    java.util.List<SaleResponse> responses =
        salePage.getContent().stream()
            .map(
                sale -> {
                  Customer customer =
                      customerRepository
                          .findById(sale.getCustomerId())
                          .orElseThrow(
                              () ->
                                  new NotFoundException(
                                      "Customer " + sale.getCustomerId() + " not found"));
                  Library library =
                      libraryRepository
                          .findById(sale.getLibraryId())
                          .orElseThrow(
                              () ->
                                  new NotFoundException(
                                      "Library " + sale.getLibraryId() + " not found"));

                  CustomerResponse customerResponse = customerMapper.toResponse(customer);
                  LibraryResponse libraryResponse =
                      new LibraryResponse(
                          library.getId(),
                          library.getName(),
                          library.getPhone(),
                          library.getEmail(),
                          library.getAddress());

                  return saleMapper.toResponse(sale, customerResponse, libraryResponse);
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

    Customer customer =
        customerRepository
            .findById(sale.getCustomerId())
            .orElseThrow(
                () -> new NotFoundException("Customer " + sale.getCustomerId() + " not found"));

    Library library =
        libraryRepository
            .findById(sale.getLibraryId())
            .orElseThrow(
                () -> new NotFoundException("Library " + sale.getLibraryId() + " not found"));

    CustomerResponse customerResponse = customerMapper.toResponse(customer);
    LibraryResponse libraryResponse =
        new LibraryResponse(
            library.getId(),
            library.getName(),
            library.getPhone(),
            library.getEmail(),
            library.getAddress());

    return saleMapper.toResponse(sale, customerResponse, libraryResponse);
  }
}
