package hei.school.library.mapper;

import hei.school.library.dto.CustomerRequest;
import hei.school.library.dto.CustomerResponse;
import hei.school.library.dto.PageResponse;
import hei.school.library.dto.PaginationDto;
import hei.school.library.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

  public CustomerResponse toResponse(Customer customer) {
    return CustomerResponse.builder()
        .id(customer.getId())
        .lastName(customer.getLastName())
        .firstName(customer.getFirstName())
        .birthDate(customer.getBirthDate())
        .email(customer.getEmail())
        .phone(customer.getPhone())
        .createdAt(customer.getCreatedAt())
        .updatedAt(customer.getUpdatedAt())
        .build();
  }

  public Customer toEntity(CustomerRequest request) {
    return new Customer(
        null,
        request.getLastName(),
        request.getFirstName(),
        request.getBirthDate(),
        request.getEmail(),
        request.getPhone(),
        null,
        null);
  }

  public PageResponse<CustomerResponse> toPageResponse(
      Page<Customer> page, int pageNum, int pageSize) {
    return PageResponse.<CustomerResponse>builder()
        .data(page.getContent().stream().map(this::toResponse).toList())
        .pagination(
            PaginationDto.builder()
                .page(pageNum)
                .size(pageSize)
                .total(page.getTotalElements())
                .build())
        .build();
  }
}
