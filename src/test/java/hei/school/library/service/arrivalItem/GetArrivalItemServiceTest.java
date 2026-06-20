package hei.school.library.service.arrivalItem;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import hei.school.library.dto.ArrivalItemResponse;
import hei.school.library.entity.Arrival;
import hei.school.library.entity.ArrivalItem;
import hei.school.library.entity.BookCopy;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.ArrivalItemMapper;
import hei.school.library.repository.dao.ArrivalItemRepository;
import hei.school.library.repository.dao.ArrivalRepository;
import hei.school.library.repository.dao.BookCopyRepository;
import hei.school.library.service.ArrivalItemService;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GetArrivalItemServiceTest {

  @Mock private ArrivalItemRepository arrivalItemRepository;

  @Mock private ArrivalRepository arrivalRepository;

  @Mock private BookCopyRepository bookCopyRepository;

  @Mock private ArrivalItemMapper arrivalItemMapper;

  @InjectMocks private ArrivalItemService arrivalItemService;

  private UUID arrivalId;
  private UUID bookCopyId;
  private ArrivalItem arrivalItem;
  private ArrivalItemResponse arrivalItemResponse;

  @BeforeEach
  void setUp() {
    arrivalId = UUID.randomUUID();
    bookCopyId = UUID.randomUUID();

    Arrival arrival = Arrival.builder().id(arrivalId).build();
    BookCopy bookCopy = BookCopy.builder().id(bookCopyId).build();

    arrivalItem =
        ArrivalItem.builder()
            .id(UUID.randomUUID())
            .arrival(arrival)
            .bookCopy(bookCopy)
            .purchasePrice(15.00)
            .quantity(3)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

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
  @DisplayName("findByArrivalId : return the list of items")
  void findByArrivalId_shouldReturnList() {
    when(arrivalRepository.existsById(arrivalId)).thenReturn(true);
    when(arrivalItemRepository.findByArrivalId(arrivalId)).thenReturn(List.of(arrivalItem));
    when(arrivalItemMapper.toResponse(arrivalItem)).thenReturn(arrivalItemResponse);

    List<ArrivalItemResponse> result = arrivalItemService.findByArrivalId(arrivalId);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getArrivalId()).isEqualTo(arrivalId);
    assertThat(result.get(0).getBookCopyId()).isEqualTo(bookCopyId);
    verify(arrivalItemRepository).findByArrivalId(arrivalId);
  }

  @Test
  @DisplayName("findByArrivalId : retrun empty list when no items")
  void findByArrivalId_shouldReturnEmptyList_whenNoItems() {
    when(arrivalRepository.existsById(arrivalId)).thenReturn(true);
    when(arrivalItemRepository.findByArrivalId(arrivalId)).thenReturn(List.of());

    List<ArrivalItemResponse> result = arrivalItemService.findByArrivalId(arrivalId);

    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("findByArrivalId : throw NotFoundException if arrival is missing")
  void findByArrivalId_shouldThrow_whenArrivalNotFound() {
    when(arrivalRepository.existsById(arrivalId)).thenReturn(false);

    assertThatThrownBy(() -> arrivalItemService.findByArrivalId(arrivalId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(arrivalId.toString());

    verify(arrivalItemRepository, never()).findByArrivalId(any());
  }
}
