package hei.school.library.service.arrivals;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import hei.school.library.dto.ArrivalRequest;
import hei.school.library.dto.ArrivalResponse;
import hei.school.library.entity.Arrival;
import hei.school.library.entity.Library;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.ArrivalMapper;
import hei.school.library.repository.dao.ArrivalRepository;
import hei.school.library.repository.dao.LibraryRepository;
import hei.school.library.service.ArrivalService;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PostArrivalServiceTest {

  @Mock private ArrivalRepository arrivalRepository;

  @Mock private LibraryRepository libraryRepository;

  @Mock private ArrivalMapper arrivalMapper;

  @InjectMocks private ArrivalService arrivalService;

  private UUID libraryId;
  private UUID arrivalId;
  private Library library;
  private Arrival arrival;
  private ArrivalResponse arrivalResponse;

  @BeforeEach
  void setUp() {
    libraryId = UUID.randomUUID();
    arrivalId = UUID.randomUUID();

    library = Library.builder().id(libraryId).build();

    arrival =
        Arrival.builder()
            .id(arrivalId)
            .library(library)
            .arrivalDate(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .build();

    arrivalResponse =
        ArrivalResponse.builder()
            .id(arrivalId)
            .libraryId(libraryId)
            .arrivalDate(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .build();
  }

  @Test
  @DisplayName("create : create arrival when arrival date provided")
  void create_shouldCreateArrival_whenArrivalDateProvided() {
    LocalDateTime arrivalDate = LocalDateTime.of(2026, 6, 10, 8, 0);
    ArrivalRequest request = new ArrivalRequest(arrivalDate);

    when(arrivalRepository.create(libraryId, arrivalDate)).thenReturn(Optional.of(arrival));

    when(arrivalMapper.toResponse(any(Arrival.class))).thenReturn(arrivalResponse);

    ArrivalResponse result = arrivalService.create(libraryId, request);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(arrivalId);
    assertThat(result.getLibraryId()).isEqualTo(libraryId);
    verify(arrivalRepository).create(libraryId, arrivalDate);
  }

  @Test
  @DisplayName("create : create arrival when arrival date not provided")
  void create_shouldCreateArrival_whenArrivalDateNotProvided() {
    ArrivalRequest request = new ArrivalRequest(null);

    when(arrivalRepository.create(eq(libraryId), any(LocalDateTime.class)))
        .thenReturn(Optional.of(arrival));

    when(arrivalMapper.toResponse(any(Arrival.class))).thenReturn(arrivalResponse);

    ArrivalResponse result = arrivalService.create(libraryId, request);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(arrivalId);
    verify(arrivalRepository).create(eq(libraryId), any(LocalDateTime.class));
  }

  @Test
  @DisplayName("create : throw not found exception when library is not found")
  void create_shouldThrow_whenLibraryNotFound() {
    ArrivalRequest request = new ArrivalRequest(LocalDateTime.now());

    when(arrivalRepository.create(eq(libraryId), any(LocalDateTime.class)))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> arrivalService.create(libraryId, request))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(libraryId.toString());

    verify(arrivalRepository).create(eq(libraryId), any(LocalDateTime.class));
  }
}
