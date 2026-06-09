package hei.school.library.service;

import hei.school.library.dto.CustomerResponse;
import hei.school.library.dto.PageResponse;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.CustomerMapper;
import hei.school.library.repository.dao.CustomerRepository;
import hei.school.library.validator.DataValidator;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CustomerService {

  private final CustomerRepository customerRepository;
  private final CustomerMapper customerMapper;
  private final DataValidator dataValidator;

  @Transactional(readOnly = true)
  public PageResponse<CustomerResponse> findAll(String search, int page, int size) {
    dataValidator.validateString("search", search);
    PageRequest pageable = PageRequest.of(page - 1, size);

    return (search == null || search.isBlank())
        ? customerMapper.toPageResponse(customerRepository.findAll(pageable), page, size)
        : customerMapper.toPageResponse(
            customerRepository.findBySearch(search, pageable), page, size);
  }

  @Transactional(readOnly = true)
  public CustomerResponse findById(UUID id) {
    return customerRepository
        .findById(id)
        .map(customerMapper::toResponse)
        .orElseThrow(() -> new NotFoundException("Customer " + id + " not found"));
  }
}
