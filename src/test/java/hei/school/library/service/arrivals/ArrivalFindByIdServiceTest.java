package hei.school.library.service.arrivals;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import hei.school.library.dto.ArrivalResponse;
import hei.school.library.entity.Arrival;
import hei.school.library.entity.Library;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.ArrivalMapper;
import hei.school.library.repository.dao.ArrivalRepository;
import hei.school.library.repository.dao.LibraryRepository;
import hei.school.library.service.ArrivalService;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ArrivalFindByIdServiceTest {

  @Mock private ArrivalRepository arrivalRepository;

  @Mock private LibraryRepository libraryRepository;

  @Mock private ArrivalMapper arrivalMapper;

  @InjectMocks private ArrivalService arrivalService;

  private UUID libraryId;
  private UUID arrivalId;
  private LocalDateTime from;
  private LocalDateTime to;
  private Arrival arrival;
  private ArrivalResponse response;

  @BeforeEach
  void setUp() {
    libraryId = UUID.randomUUID();
    arrivalId = UUID.randomUUID();
    from = LocalDateTime.of(2025, 1, 1, 0, 0);
    to = LocalDateTime.of(2025, 12, 31, 23, 59);

    arrival =
        Arrival.builder()
            .id(arrivalId)
            .library(Library.builder().id(libraryId).build())
            .arrivalDate(LocalDateTime.of(2025, 6, 1, 10, 0))
            .createdAt(LocalDateTime.of(2025, 6, 1, 10, 0))
            .build();

    response = new ArrivalResponse();
  }

  @Test
  @DisplayName("findById: return ArrivalResponse when the library and arrival exist")
  void findById_shouldReturnArrivalResponse_whenBothExist() {
    when(libraryRepository.existsById(libraryId)).thenReturn(true);
    when(arrivalRepository.findById(arrivalId)).thenReturn(Optional.of(arrival));
    when(arrivalMapper.toResponse(arrival)).thenReturn(response);

    ArrivalResponse result = arrivalService.findById(libraryId, arrivalId);

    assertThat(result).isNotNull().isEqualTo(response);

    verify(libraryRepository).existsById(libraryId);
    verify(arrivalRepository).findById(arrivalId);
    verify(arrivalMapper).toResponse(arrival);
  }

  @Test
  @DisplayName("findById: throw not found exception when library is not found")
  void findById_shouldThrowNotFoundException_whenLibraryNotFound() {
    when(libraryRepository.existsById(libraryId)).thenReturn(false);

    assertThatThrownBy(() -> arrivalService.findById(libraryId, arrivalId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(libraryId.toString());

    verify(libraryRepository).existsById(libraryId);
    verifyNoInteractions(arrivalRepository);
    verifyNoInteractions(arrivalMapper);
  }

  @Test
  @DisplayName("findById: throw not found exception when arrival is not found")
  void findById_shouldThrowNotFoundException_whenArrivalNotFound() {
    when(libraryRepository.existsById(libraryId)).thenReturn(true);
    when(arrivalRepository.findById(arrivalId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> arrivalService.findById(libraryId, arrivalId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(arrivalId.toString());

    verify(arrivalRepository).findById(arrivalId);
    verifyNoInteractions(arrivalMapper);
  }
}
