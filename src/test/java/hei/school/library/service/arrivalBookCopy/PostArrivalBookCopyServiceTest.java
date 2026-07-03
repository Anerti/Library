package hei.school.library.service.arrivalBookCopy;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import hei.school.library.dto.ArrivalBookCopyRequest;
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
public class PostArrivalBookCopyServiceTest {

  @Mock private ArrivalBookCopyRepository arrivalBookCopyRepository;

  @Mock private ArrivalRepository arrivalRepository;

  @Mock private BookCopyRepository bookCopyRepository;

  @Mock private ArrivalBookCopyMapper arrivalBookCopyMapper;

  @InjectMocks private ArrivalBookCopyService arrivalBookCopyService;

  private UUID arrivalId;
  private UUID bookCopyId;
  private Arrival arrival;
  private BookCopy bookCopy;
  private ArrivalBookCopy arrivalBookCopy;
  private ArrivalBookCopyResponse arrivalBookCopyResponse;
  private ArrivalBookCopyRequest request;

  @BeforeEach
  void setUp() {
    arrivalId = UUID.randomUUID();
    bookCopyId = UUID.randomUUID();

    arrival = Arrival.builder().id(arrivalId).build();
    bookCopy = BookCopy.builder().id(bookCopyId).build();

    request = new ArrivalBookCopyRequest(bookCopyId, 15.00, 3);

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
  @DisplayName("create : should create and return item")
  void create_shouldCreateAndReturnItem() {
    when(arrivalRepository.findById(arrivalId)).thenReturn(Optional.of(arrival));
    when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.of(bookCopy));
    when(arrivalBookCopyRepository.save(any(ArrivalBookCopy.class))).thenReturn(arrivalBookCopy);
    when(arrivalBookCopyMapper.toResponse(arrivalBookCopy)).thenReturn(arrivalBookCopyResponse);

    ArrivalBookCopyResponse result = arrivalBookCopyService.create(arrivalId, request);

    assertThat(result).isNotNull();
    assertThat(result.getArrivalId()).isEqualTo(arrivalId);
    assertThat(result.getBookCopyId()).isEqualTo(bookCopyId);
    assertThat(result.getQuantity()).isEqualTo(3);
    verify(arrivalBookCopyRepository).save(any(ArrivalBookCopy.class));
  }

  @Test
  @DisplayName("create : use default quantity when not provided")
  void create_shouldUseDefaultQuantity_whenNotProvided() {
    ArrivalBookCopyRequest requestWithoutQuantity = new ArrivalBookCopyRequest(bookCopyId, 15.00, null);

    when(arrivalRepository.findById(arrivalId)).thenReturn(Optional.of(arrival));
    when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.of(bookCopy));
    when(arrivalBookCopyRepository.save(any(ArrivalBookCopy.class))).thenReturn(arrivalBookCopy);
    when(arrivalBookCopyMapper.toResponse(arrivalBookCopy)).thenReturn(arrivalBookCopyResponse);

    arrivalBookCopyService.create(arrivalId, requestWithoutQuantity);

    verify(arrivalBookCopyRepository).save(argThat(item -> item.getQuantity() == 1));
  }

  @Test
  @DisplayName("create : throw NotFoundException if arrival is missing")
  void create_shouldThrow_whenArrivalNotFound() {
    when(arrivalRepository.findById(arrivalId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> arrivalBookCopyService.create(arrivalId, request))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(arrivalId.toString());

    verify(arrivalBookCopyRepository, never()).save(any());
  }

  @Test
  @DisplayName("create : throw NotFoundException if bookCopy is missing")
  void create_shouldThrow_whenBookCopyNotFound() {
    when(arrivalRepository.findById(arrivalId)).thenReturn(Optional.of(arrival));
    when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> arrivalBookCopyService.create(arrivalId, request))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining(bookCopyId.toString());

    verify(arrivalBookCopyRepository, never()).save(any());
  }
}
