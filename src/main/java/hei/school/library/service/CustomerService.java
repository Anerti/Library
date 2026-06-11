package hei.school.library.service;

import hei.school.library.dto.CustomerRequest;
import hei.school.library.dto.CustomerResponse;
import hei.school.library.dto.CustomerUpdateRequest;
import hei.school.library.dto.PageResponse;
import hei.school.library.entity.Customer;
import hei.school.library.exception.ConflictException;
import hei.school.library.exception.ConflictException;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.CustomerMapper;
import hei.school.library.repository.dao.CustomerRepository;
import hei.school.library.validator.CustomerValidator;
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
  private final CustomerValidator customerValidator;

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

  @Transactional
  public CustomerResponse create(CustomerRequest request) {
    dataValidator.validateCustomer(request);

    return customerRepository
        .create(
            request.getLastName(),
            request.getFirstName(),
            request.getBirthDate(),
            request.getEmail(),
            request.getPhone())
        .map(customerMapper::toResponse)
        .orElseThrow(
            () ->
                new ConflictException("Customer email " + request.getEmail() + " already exists"));
  }

  @Transactional
  public CustomerResponse update(UUID id, CustomerUpdateRequest request) {
    customerValidator.validateUpdate(request);

    Customer customer =
        customerRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Customer " + id + " not found"));

    if (request.getLastName() != null) customer.setLastName(request.getLastName());
    if (request.getFirstName() != null) customer.setFirstName(request.getFirstName());
    if (request.getBirthDate() != null) customer.setBirthDate(request.getBirthDate());
    if (request.getEmail() != null) customer.setEmail(request.getEmail());
    if (request.getPhone() != null) customer.setPhone(request.getPhone());

    return customerMapper.toResponse(customerRepository.save(customer));
  }
}
