package hei.school.library.mapper;

import hei.school.library.dto.CustomerResponse;
import hei.school.library.dto.PageResponse;
import hei.school.library.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerMapper {

  private final PaginationMapper paginationMapper;

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

  public PageResponse<CustomerResponse> toPageResponse(
      Page<Customer> page, int pageNum, int pageSize) {
    return PageResponse.<CustomerResponse>builder()
        .data(page.getContent().stream().map(this::toResponse).toList())
        .pagination(paginationMapper.toPaginationDto(page, pageNum, pageSize))
        .build();
  }
}
