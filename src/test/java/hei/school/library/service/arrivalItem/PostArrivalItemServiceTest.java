package hei.school.library.service.arrivalItem;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import hei.school.library.dto.ArrivalItemRequest;
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
public class PostArrivalItemServiceTest {

  @Mock private ArrivalItemRepository arrivalItemRepository;

  @Mock private ArrivalRepository arrivalRepository;

  @Mock private BookCopyRepository bookCopyRepository;

  @Mock private ArrivalItemMapper arrivalItemMapper;

  @InjectMocks private ArrivalItemService arrivalItemService;

  private UUID arrivalId;
  private UUID bookCopyId;
  private Arrival arrival;
  private BookCopy bookCopy;
  private ArrivalItem arrivalItem;
  private ArrivalItemResponse arrivalItemResponse;
  private ArrivalItemRequest request;

  @BeforeEach
  void setUp() {
    arrivalId = UUID.randomUUID();
    bookCopyId = UUID.randomUUID();

    arrival = Arrival.builder().id(arrivalId).build();
    bookCopy = BookCopy.builder().id(bookCopyId).build();

    request = new ArrivalItemRequest(bookCopyId, 15.00, 3);

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
  @DisplayName("create : should create and return item")
  void create_shouldCreateAndReturnItem() {
    when(arrivalRepository.findById(arrivalId)).thenReturn(Optional.of(arrival));
    when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.of(bookCopy));
    when(arrivalItemRepository.save(any(ArrivalItem.class))).thenReturn(arrivalItem);
    when(arrivalItemMapper.toResponse(arrivalItem)).thenReturn(arrivalItemResponse);

    ArrivalItemResponse result = arrivalItemService.create(arrivalId, request);

    assertThat(result).isNotNull();
    assertThat(result.getArrivalId()).isEqualTo(arrivalId);
    assertThat(result.getBookCopyId()).isEqualTo(bookCopyId);
    assertThat(result.getQuantity()).isEqualTo(3);
    verify(arrivalItemRepository).save(any(ArrivalItem.class));
  }

  @Test
  @DisplayName("create : use default quantity when not provided")
  void create_shouldUseDefaultQuantity_whenNotProvided() {
    ArrivalItemRequest requestWithoutQuantity = new ArrivalItemRequest(bookCopyId, 15.00, null);

    when(arrivalRepository.findById(arrivalId)).thenReturn(Optional.of(arrival));
    when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.of(bookCopy));
    when(arrivalItemRepository.save(any(ArrivalItem.class))).thenReturn(arrivalItem);
    when(arrivalItemMapper.toResponse(arrivalItem)).thenReturn(arrivalItemResponse);

    arrivalItemService.create(arrivalId, requestWithoutQuantity);

    verify(arrivalItemRepository).save(argThat(item -> item.getQuantity() == 1));
  }

  @Test
  @DisplayName("create : throw NotFoundException if arrival is missing")
  void create_shouldThrow_whenArrivalNotFound() {
    when(arrivalRepository.findById(arrivalId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> arrivalItemService.create(arrivalId, request))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(arrivalId.toString());

    verify(arrivalItemRepository, never()).save(any());
  }

  @Test
  @DisplayName("create : throw NotFoundException if bookCopy is missing")
  void create_shouldThrow_whenBookCopyNotFound() {
    when(arrivalRepository.findById(arrivalId)).thenReturn(Optional.of(arrival));
    when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> arrivalItemService.create(arrivalId, request))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(bookCopyId.toString());

    verify(arrivalItemRepository, never()).save(any());
  }
}
