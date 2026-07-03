package hei.school.library.service.arrivalBookCopy;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import hei.school.library.dto.ArrivalBookCopyResponse;
import hei.school.library.entity.Arrival;
import hei.school.library.entity.ArrivalBookCopy;
import hei.school.library.entity.BookCopy;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.ArrivalBookCopyMapper;
import hei.school.library.repository.dao.ArrivalBookCopyRepository;
import hei.school.library.repository.dao.ArrivalRepository;
import hei.school.library.repository.dao.BookCopyRepository;
import hei.school.library.service.ArrivalBookCopyService;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GetArrivalBookCopyServiceTest {

  @Mock private ArrivalBookCopyRepository arrivalBookCopyRepository;

  @Mock private ArrivalRepository arrivalRepository;

  @Mock private BookCopyRepository bookCopyRepository;

  @Mock private ArrivalBookCopyMapper arrivalBookCopyMapper;

  @InjectMocks private ArrivalBookCopyService arrivalBookCopyService;

  private UUID arrivalId;
  private UUID bookCopyId;
  private ArrivalBookCopy arrivalBookCopy;
  private ArrivalBookCopyResponse arrivalBookCopyResponse;

  @BeforeEach
  void setUp() {
    arrivalId = UUID.randomUUID();
    bookCopyId = UUID.randomUUID();

    Arrival arrival = Arrival.builder().id(arrivalId).build();
    BookCopy bookCopy = BookCopy.builder().id(bookCopyId).build();

    arrivalBookCopy =
        ArrivalBookCopy.builder()
            .id(UUID.randomUUID())
            .arrival(arrival)
            .bookCopy(bookCopy)
            .purchasePrice(15.00)
            .quantity(3)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    arrivalBookCopyResponse =
        ArrivalBookCopyResponse.builder()
            .arrivalId(arrivalId)
            .bookCopyId(bookCopyId)
            .purchasePrice(15.00)
            .quantity(3)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
  }

  @Test
  @DisplayName("findByArrivalId : return the list of items")
  void findByArrivalId_shouldReturnList() {
    when(arrivalRepository.existsById(arrivalId)).thenReturn(true);
    when(arrivalBookCopyRepository.findByArrivalId(arrivalId)).thenReturn(List.of(arrivalBookCopy));
    when(arrivalBookCopyMapper.toResponse(arrivalBookCopy)).thenReturn(arrivalBookCopyResponse);

    List<ArrivalBookCopyResponse> result = arrivalBookCopyService.findByArrivalId(arrivalId);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getArrivalId()).isEqualTo(arrivalId);
    assertThat(result.get(0).getBookCopyId()).isEqualTo(bookCopyId);
    verify(arrivalBookCopyRepository).findByArrivalId(arrivalId);
  }

  @Test
  @DisplayName("findByArrivalId : return empty list when no items")
  void findByArrivalId_shouldReturnEmptyList_whenNoItems() {
    when(arrivalRepository.existsById(arrivalId)).thenReturn(true);
    when(arrivalBookCopyRepository.findByArrivalId(arrivalId)).thenReturn(List.of());

    List<ArrivalBookCopyResponse> result = arrivalBookCopyService.findByArrivalId(arrivalId);

    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("findByArrivalId : throw NotFoundException if arrival is missing")
  void findByArrivalId_shouldThrow_whenArrivalNotFound() {
    when(arrivalRepository.existsById(arrivalId)).thenReturn(false);

    assertThatThrownBy(() -> arrivalBookCopyService.findByArrivalId(arrivalId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(arrivalId.toString());

    verify(arrivalBookCopyRepository, never()).findByArrivalId(any());
  }
}
