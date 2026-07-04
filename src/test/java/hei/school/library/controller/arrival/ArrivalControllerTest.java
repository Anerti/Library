package hei.school.library.controller.arrival;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import hei.school.library.config.JwtTokenProvider;
import hei.school.library.dto.ArrivalRequest;
import hei.school.library.dto.ArrivalResponse;
import hei.school.library.dto.PageResponse;
import hei.school.library.endpoint.rest.controller.ArrivalController;
import hei.school.library.exception.GlobalExceptionHandler;
import hei.school.library.exception.NotFoundException;
import hei.school.library.service.ArrivalService;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({ArrivalController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
public class ArrivalControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private ArrivalService arrivalService;
  @MockBean private JwtTokenProvider jwtTokenProvider;

  private UUID libraryId;
  private UUID arrivalId;
  private ArrivalResponse arrivalResponse;

  @BeforeEach
  void setUp() {
    libraryId = UUID.randomUUID();
    arrivalId = UUID.randomUUID();

    arrivalResponse =
        ArrivalResponse.builder()
            .id(arrivalId)
            .libraryId(libraryId)
            .arrivalDate(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .build();
  }

  @Test
  void should_get_arrivals_with_pagination() throws Exception {
    PageResponse<ArrivalResponse> mockPageResponse = new PageResponse<>();

    when(arrivalService.findByLibraryId(libraryId, null, null, 1, 20)).thenReturn(mockPageResponse);

    mockMvc.perform(get("/libraries/{libraryId}/arrivals", libraryId)).andExpect(status().isOk());

    verify(arrivalService).findByLibraryId(libraryId, null, null, 1, 20);
  }

  @Test
  void should_get_arrivals_filtered_by_date_range() throws Exception {
    LocalDateTime from = LocalDateTime.of(2026, 1, 1, 0, 0);
    LocalDateTime to = LocalDateTime.of(2026, 12, 31, 23, 59);
    PageResponse<ArrivalResponse> mockPageResponse = new PageResponse<>();

    when(arrivalService.findByLibraryId(libraryId, from, to, 1, 20)).thenReturn(mockPageResponse);

    mockMvc
        .perform(
            get("/libraries/{libraryId}/arrivals", libraryId)
                .param("from", from.toString())
                .param("to", to.toString()))
        .andExpect(status().isOk());

    verify(arrivalService).findByLibraryId(libraryId, from, to, 1, 20);
  }

  @Test
  void should_return_not_found_when_library_does_not_exist_on_list() throws Exception {
    when(arrivalService.findByLibraryId(libraryId, null, null, 1, 20))
        .thenThrow(new NotFoundException("Library with id " + libraryId + " not found"));

    mockMvc
        .perform(get("/libraries/{libraryId}/arrivals", libraryId))
        .andExpect(status().isNotFound());
  }

  @Test
  void should_get_arrival_by_id() throws Exception {
    when(arrivalService.findById(libraryId, arrivalId)).thenReturn(arrivalResponse);

    mockMvc
        .perform(get("/libraries/{libraryId}/arrivals/{arrivalId}", libraryId, arrivalId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(arrivalId.toString()))
        .andExpect(jsonPath("$.libraryId").value(libraryId.toString()));
  }

  @Test
  void should_return_not_found_when_arrival_does_not_exist() throws Exception {
    when(arrivalService.findById(libraryId, arrivalId))
        .thenThrow(new NotFoundException("Arrival with id " + arrivalId + " not found"));

    mockMvc
        .perform(get("/libraries/{libraryId}/arrivals/{arrivalId}", libraryId, arrivalId))
        .andExpect(status().isNotFound());
  }

  @Test
  void should_create_arrival() throws Exception {
    LocalDateTime arrivalDate = LocalDateTime.of(2026, 6, 10, 8, 0);
    ArrivalRequest request = new ArrivalRequest(arrivalDate);

    when(arrivalService.create(eq(libraryId), any(ArrivalRequest.class)))
        .thenReturn(arrivalResponse);

    mockMvc
        .perform(
            post("/libraries/{libraryId}/arrivals", libraryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(arrivalId.toString()))
        .andExpect(jsonPath("$.libraryId").value(libraryId.toString()));
  }

  @Test
  void should_create_arrival_without_arrival_date() throws Exception {
    ArrivalRequest request = new ArrivalRequest(null);

    when(arrivalService.create(eq(libraryId), any(ArrivalRequest.class)))
        .thenReturn(arrivalResponse);

    mockMvc
        .perform(
            post("/libraries/{libraryId}/arrivals", libraryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());
  }

  @Test
  void should_return_not_found_when_library_does_not_exist_on_create() throws Exception {
    ArrivalRequest request = new ArrivalRequest(LocalDateTime.now());

    when(arrivalService.create(eq(libraryId), any(ArrivalRequest.class)))
        .thenThrow(new NotFoundException("Library with id " + libraryId + " not found"));

    mockMvc
        .perform(
            post("/libraries/{libraryId}/arrivals", libraryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }
}
