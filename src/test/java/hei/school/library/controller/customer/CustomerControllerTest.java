package hei.school.library.controller.customer;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import hei.school.library.dto.CustomerRequest;
import hei.school.library.dto.CustomerResponse;
import hei.school.library.dto.CustomerUpdateRequest;
import hei.school.library.dto.PageResponse;
import hei.school.library.endpoint.rest.controller.CustomerController;
import hei.school.library.exception.GlobalExceptionHandler;
import hei.school.library.exception.NotFoundException;
import hei.school.library.service.CustomerService;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({CustomerController.class, GlobalExceptionHandler.class})
public class CustomerControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private CustomerService customerService;

  private UUID customerId;
  private CustomerResponse customerResponse;

  @BeforeEach
  void setUp() {
    customerId = UUID.randomUUID();

    customerResponse =
        CustomerResponse.builder()
            .id(customerId)
            .lastName("Randria")
            .firstName("Faly")
            .birthDate(LocalDate.of(1995, 8, 12))
            .email("faly.randria@email.com")
            .phone("+261 32 11 234 56")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
  }

  @Test
  void should_get_all_customers_with_pagination() throws Exception {
    String search = "Randria";
    PageResponse<CustomerResponse> mockPageResponse = new PageResponse<>();

    when(customerService.findAll(search, 1, 20)).thenReturn(mockPageResponse);

    mockMvc
        .perform(get("/customers").param("search", search).param("page", "1").param("size", "20"))
        .andExpect(status().isOk());

    verify(customerService).findAll(search, 1, 20);
  }

  @Test
  void should_use_default_pagination_when_not_provided() throws Exception {
    PageResponse<CustomerResponse> mockPageResponse = new PageResponse<>();

    when(customerService.findAll(null, 1, 20)).thenReturn(mockPageResponse);

    mockMvc.perform(get("/customers")).andExpect(status().isOk());

    verify(customerService).findAll(null, 1, 20);
  }

  @Test
  void should_get_customer_by_id() throws Exception {
    when(customerService.findById(customerId)).thenReturn(customerResponse);

    mockMvc
        .perform(get("/customers/{id}", customerId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(customerId.toString()))
        .andExpect(jsonPath("$.lastName").value("Randria"))
        .andExpect(jsonPath("$.firstName").value("Faly"))
        .andExpect(jsonPath("$.email").value("faly.randria@email.com"));
  }

  @Test
  void should_return_not_found_when_customer_does_not_exist() throws Exception {
    when(customerService.findById(customerId))
        .thenThrow(new NotFoundException("Customer " + customerId + " not found"));

    mockMvc.perform(get("/customers/{id}", customerId)).andExpect(status().isNotFound());
  }

  @Test
  void should_create_customer() throws Exception {
    CustomerRequest request =
        new CustomerRequest(
            "Randria",
            "Faly",
            LocalDate.of(1995, 8, 12),
            "faly.randria@email.com",
            "+261 32 11 234 56");

    when(customerService.create(any(CustomerRequest.class))).thenReturn(customerResponse);

    mockMvc
        .perform(
            post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(customerId.toString()))
        .andExpect(jsonPath("$.lastName").value("Randria"))
        .andExpect(jsonPath("$.email").value("faly.randria@email.com"));
  }

  @Test
  void should_update_customer() throws Exception {
    CustomerUpdateRequest request =
        new CustomerUpdateRequest(null, "Faly Updated", null, null, null);

    CustomerResponse updatedResponse =
        CustomerResponse.builder()
            .id(customerId)
            .lastName("Randria")
            .firstName("Faly Updated")
            .birthDate(LocalDate.of(1995, 8, 12))
            .email("faly.randria@email.com")
            .phone("+261 32 11 234 56")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    when(customerService.update(eq(customerId), any(CustomerUpdateRequest.class)))
        .thenReturn(updatedResponse);

    mockMvc
        .perform(
            patch("/customers/{id}", customerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName").value("Faly Updated"));
  }

  @Test
  void should_return_not_found_when_customer_does_not_exist_on_update() throws Exception {
    CustomerUpdateRequest request =
        new CustomerUpdateRequest(null, "Faly Updated", null, null, null);

    when(customerService.update(eq(customerId), any(CustomerUpdateRequest.class)))
        .thenThrow(new NotFoundException("Customer " + customerId + " not found"));

    mockMvc
        .perform(
            patch("/customers/{id}", customerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  void should_delete_customer() throws Exception {
    doNothing().when(customerService).delete(customerId);

    mockMvc.perform(delete("/customers/{id}", customerId)).andExpect(status().isNoContent());

    verify(customerService).delete(customerId);
  }

  @Test
  void should_return_not_found_when_customer_does_not_exist_on_delete() throws Exception {
    doThrow(new NotFoundException("Customer " + customerId + " not found"))
        .when(customerService)
        .delete(customerId);

    mockMvc.perform(delete("/customers/{id}", customerId)).andExpect(status().isNotFound());
  }
}
