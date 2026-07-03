package hei.school.library.service.arrivalBookCopy;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import hei.school.library.entity.Arrival;
import hei.school.library.entity.ArrivalBookCopy;
import hei.school.library.entity.BookCopy;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.ArrivalBookCopyMapper;
import hei.school.library.repository.dao.ArrivalBookCopyRepository;
import hei.school.library.repository.dao.ArrivalRepository;
import hei.school.library.repository.dao.BookCopyRepository;
import hei.school.library.service.ArrivalBookCopyService;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DeleteArrivalBookCopyServiceTest {

  @Mock private ArrivalBookCopyRepository arrivalBookCopyRepository;

  @Mock private ArrivalRepository arrivalRepository;

  @Mock private BookCopyRepository bookCopyRepository;

  @Mock private ArrivalBookCopyMapper arrivalBookCopyMapper;

  @InjectMocks private ArrivalBookCopyService arrivalBookCopyService;

  private UUID arrivalId;
  private UUID bookCopyId;
  private ArrivalBookCopy arrivalBookCopy;

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
            .build();
  }

  @Test
  @DisplayName("delete : should delete when item exists")
  void delete_shouldDelete_whenExists() {
    when(arrivalBookCopyRepository.findByArrivalIdAndBookCopyId(arrivalId, bookCopyId))
        .thenReturn(Optional.of(arrivalBookCopy));

    arrivalBookCopyService.delete(arrivalId, bookCopyId);

    verify(arrivalBookCopyRepository).delete(arrivalBookCopy);
  }

  @Test
  @DisplayName("delete : should throw NotFoundException when item does not exist")
  void delete_shouldThrow_whenNotFound() {
    when(arrivalBookCopyRepository.findByArrivalIdAndBookCopyId(arrivalId, bookCopyId))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> arrivalBookCopyService.delete(arrivalId, bookCopyId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(arrivalId.toString())
        .hasMessageContaining(bookCopyId.toString());

    verify(arrivalBookCopyRepository, never()).delete(any());
  }
}
