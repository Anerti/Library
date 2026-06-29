package hei.school.library.controller.arrivalItem;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import hei.school.library.dto.ArrivalItemRequest;
import hei.school.library.dto.ArrivalItemResponse;
import hei.school.library.endpoint.rest.controller.ArrivalItemController;
import hei.school.library.exception.GlobalExceptionHandler;
import hei.school.library.exception.NotFoundException;
import hei.school.library.config.JwtTokenProvider;
import hei.school.library.service.ArrivalItemService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({ArrivalItemController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
public class ArrivalItemControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private ArrivalItemService arrivalItemService;
  @MockBean private JwtTokenProvider jwtTokenProvider;

  private UUID arrivalId;
  private UUID bookCopyId;
  private ArrivalItemResponse arrivalItemResponse;

  @BeforeEach
  void setUp() {
    arrivalId = UUID.randomUUID();
    bookCopyId = UUID.randomUUID();

    arrivalItemResponse =
        ArrivalItemResponse.builder()
            .arrivalId(arrivalId)
            .bookCopyId(bookCopyId)
            .purchasePrice(15.00)
            .quantity(3)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
  }

  @Test
  @DisplayName("findByArrivalId : should return 200 with list of items")
  void findByArrivalId_shouldReturn200() throws Exception {
    when(arrivalItemService.findByArrivalId(arrivalId)).thenReturn(List.of(arrivalItemResponse));

    mockMvc
        .perform(get("/arrivals/{arrivalId}/items", arrivalId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].arrivalId").value(arrivalId.toString()))
        .andExpect(jsonPath("$[0].bookCopyId").value(bookCopyId.toString()))
        .andExpect(jsonPath("$[0].quantity").value(3));

    verify(arrivalItemService).findByArrivalId(arrivalId);
  }

  @Test
  @DisplayName("findByArrivalId : should return 200 with empty list when no items")
  void findByArrivalId_shouldReturnEmptyList() throws Exception {
    when(arrivalItemService.findByArrivalId(arrivalId)).thenReturn(List.of());

    mockMvc
        .perform(get("/arrivals/{arrivalId}/items", arrivalId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").isEmpty());
  }

  @Test
  @DisplayName("findByArrivalId : should return 404 when arrival not found")
  void findByArrivalId_shouldReturn404_whenArrivalNotFound() throws Exception {
    when(arrivalItemService.findByArrivalId(arrivalId))
        .thenThrow(new NotFoundException("Arrival with id " + arrivalId + " not found"));

    mockMvc.perform(get("/arrivals/{arrivalId}/items", arrivalId)).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("create : should return 201 with created item")
  void create_shouldReturn201() throws Exception {
    ArrivalItemRequest request = new ArrivalItemRequest(bookCopyId, 15.00, 3);

    when(arrivalItemService.create(eq(arrivalId), any(ArrivalItemRequest.class)))
        .thenReturn(arrivalItemResponse);

    mockMvc
        .perform(
            post("/arrivals/{arrivalId}/items", arrivalId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.arrivalId").value(arrivalId.toString()))
        .andExpect(jsonPath("$.bookCopyId").value(bookCopyId.toString()))
        .andExpect(jsonPath("$.quantity").value(3));

    verify(arrivalItemService).create(eq(arrivalId), any(ArrivalItemRequest.class));
  }

  @Test
  @DisplayName("create : should return 404 when arrival not found")
  void create_shouldReturn404_whenArrivalNotFound() throws Exception {
    ArrivalItemRequest request = new ArrivalItemRequest(bookCopyId, 15.00, 3);

    when(arrivalItemService.create(eq(arrivalId), any(ArrivalItemRequest.class)))
        .thenThrow(new NotFoundException("Arrival with id " + arrivalId + " not found"));

    mockMvc
        .perform(
            post("/arrivals/{arrivalId}/items", arrivalId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("create : should return 404 when bookCopy not found")
  void create_shouldReturn404_whenBookCopyNotFound() throws Exception {
    ArrivalItemRequest request = new ArrivalItemRequest(bookCopyId, 15.00, 3);

    when(arrivalItemService.create(eq(arrivalId), any(ArrivalItemRequest.class)))
        .thenThrow(new NotFoundException("BookCopy with id " + bookCopyId + " not found"));

    mockMvc
        .perform(
            post("/arrivals/{arrivalId}/items", arrivalId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("delete : should return 204 when item deleted")
  void delete_shouldReturn204() throws Exception {
    doNothing().when(arrivalItemService).delete(arrivalId, bookCopyId);

    mockMvc
        .perform(delete("/arrivals/{arrivalId}/items/{bookCopyId}", arrivalId, bookCopyId))
        .andExpect(status().isNoContent());

    verify(arrivalItemService).delete(arrivalId, bookCopyId);
  }

  @Test
  @DisplayName("delete : should return 404 when item not found")
  void delete_shouldReturn404_whenNotFound() throws Exception {
    doThrow(
            new NotFoundException(
                "ArrivalItem not found for arrival " + arrivalId + " and bookCopy " + bookCopyId))
        .when(arrivalItemService)
        .delete(arrivalId, bookCopyId);

    mockMvc
        .perform(delete("/arrivals/{arrivalId}/items/{bookCopyId}", arrivalId, bookCopyId))
        .andExpect(status().isNotFound());
  }
}
