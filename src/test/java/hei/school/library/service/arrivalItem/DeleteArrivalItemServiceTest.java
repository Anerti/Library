package hei.school.library.service.arrivalItem;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import hei.school.library.entity.Arrival;
import hei.school.library.entity.ArrivalItem;
import hei.school.library.entity.BookCopy;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.ArrivalItemMapper;
import hei.school.library.repository.dao.ArrivalItemRepository;
import hei.school.library.repository.dao.ArrivalRepository;
import hei.school.library.repository.dao.BookCopyRepository;
import hei.school.library.service.ArrivalItemService;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DeleteArrivalItemServiceTest {

  @Mock private ArrivalItemRepository arrivalItemRepository;

  @Mock private ArrivalRepository arrivalRepository;

  @Mock private BookCopyRepository bookCopyRepository;

  @Mock private ArrivalItemMapper arrivalItemMapper;

  @InjectMocks private ArrivalItemService arrivalItemService;

  private UUID arrivalId;
  private UUID bookCopyId;
  private ArrivalItem arrivalItem;

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
            .build();
  }

  @Test
  @DisplayName("delete : should delete when item exists")
  void delete_shouldDelete_whenExists() {
    when(arrivalItemRepository.findByArrivalIdAndBookCopyId(arrivalId, bookCopyId))
        .thenReturn(Optional.of(arrivalItem));

    arrivalItemService.delete(arrivalId, bookCopyId);

    verify(arrivalItemRepository).delete(arrivalItem);
  }

  @Test
  @DisplayName("delete : should throw NotFoundException when item does not exist")
  void delete_shouldThrow_whenNotFound() {
    when(arrivalItemRepository.findByArrivalIdAndBookCopyId(arrivalId, bookCopyId))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> arrivalItemService.delete(arrivalId, bookCopyId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(arrivalId.toString())
        .hasMessageContaining(bookCopyId.toString());

    verify(arrivalItemRepository, never()).delete(any());
  }
}
