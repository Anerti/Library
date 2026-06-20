package hei.school.library.service.arrivals;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import hei.school.library.dto.ArrivalResponse;
import hei.school.library.dto.PageResponse;
import hei.school.library.entity.Arrival;
import hei.school.library.entity.Library;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.ArrivalMapper;
import hei.school.library.repository.dao.ArrivalRepository;
import hei.school.library.repository.dao.LibraryRepository;
import hei.school.library.service.ArrivalService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

@ExtendWith(MockitoExtension.class)
public class ArrivalFindByLibraryIdServiceTest {
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
  @DisplayName("find_by_library_Id : should return page response when library exist")
  void findByLibraryId_shouldReturnPageResponse_whenLibraryExists() {
    Page<Arrival> page = new PageImpl<>(List.of(arrival));

    when(libraryRepository.existsById(libraryId)).thenReturn(true);
    when(arrivalRepository.findByLibraryIdAndDateRange(
            eq(libraryId), eq(from), eq(to), any(Pageable.class)))
        .thenReturn(page);
    when(arrivalMapper.toResponse(arrival)).thenReturn(response);

    PageResponse<ArrivalResponse> result =
        arrivalService.findByLibraryId(libraryId, from, to, 1, 10);

    assertThat(result).isNotNull();
    assertThat(result.getData()).hasSize(1).containsExactly(response);
    assertThat(result.getPagination().getTotal()).isEqualTo(1L);

    verify(libraryRepository).existsById(libraryId);
    verify(arrivalRepository)
        .findByLibraryIdAndDateRange(eq(libraryId), eq(from), eq(to), any(Pageable.class));
    verify(arrivalMapper).toResponse(arrival);
  }

  @Test
  @DisplayName("find_by_library_Id: should return empty page when no arrivals found")
  void findByLibraryId_shouldReturnEmptyPage_whenNoArrivalsFound() {
    Page<Arrival> emptyPage = new PageImpl<>(List.of());

    when(libraryRepository.existsById(libraryId)).thenReturn(true);
    when(arrivalRepository.findByLibraryIdAndDateRange(
            eq(libraryId), eq(from), eq(to), any(Pageable.class)))
        .thenReturn(emptyPage);

    PageResponse<ArrivalResponse> result =
        arrivalService.findByLibraryId(libraryId, from, to, 1, 10);

    assertThat(result.getData()).isEmpty();
    assertThat(result.getPagination().getTotal()).isZero();

    verify(arrivalMapper, never()).toResponse(any());
  }

  @Test
  @DisplayName("find_by_library_Id: should throw not found exception when library not found")
  void findByLibraryId_shouldThrowNotFoundException_whenLibraryNotFound() {
    when(libraryRepository.existsById(libraryId)).thenReturn(false);

    assertThatThrownBy(() -> arrivalService.findByLibraryId(libraryId, from, to, 1, 10))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(libraryId.toString());

    verify(libraryRepository).existsById(libraryId);
    verifyNoInteractions(arrivalRepository);
    verifyNoInteractions(arrivalMapper);
  }

  @Test
  @DisplayName("find_by_library_Id: return arrival when library and arrival exist")
  void findByLibraryId_shouldWork_whenFromAndToAreNull() {

    Page<Arrival> page = new PageImpl<>(List.of(arrival));

    when(libraryRepository.existsById(libraryId)).thenReturn(true);
    when(arrivalRepository.findByLibraryIdAndDateRange(
            eq(libraryId), isNull(), isNull(), any(Pageable.class)))
        .thenReturn(page);
    when(arrivalMapper.toResponse(arrival)).thenReturn(response);

    PageResponse<ArrivalResponse> result =
        arrivalService.findByLibraryId(libraryId, null, null, 1, 10);

    assertThat(result.getData()).hasSize(1);
    assertThat(result.getPagination().getTotal()).isEqualTo(1L);

    verify(arrivalRepository)
        .findByLibraryIdAndDateRange(eq(libraryId), isNull(), isNull(), any(Pageable.class));
  }
}
