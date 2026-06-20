package hei.school.library.service;

import hei.school.library.dto.ArrivalItemResponse;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.ArrivalItemMapper;
import hei.school.library.repository.dao.ArrivalItemRepository;
import hei.school.library.repository.dao.ArrivalRepository;
import hei.school.library.repository.dao.BookCopyRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArrivalItemService {
  private final ArrivalItemRepository arrivalItemRepository;
  private final ArrivalRepository arrivalRepository;
  private final BookCopyRepository bookCopyRepository;
  private final ArrivalItemMapper arrivalItemMapper;

  public List<ArrivalItemResponse> findByArrivalId(UUID arrivalId) {
    if (!arrivalRepository.existsById(arrivalId)) {
      throw new NotFoundException("Arrival with id " + arrivalId + " not found");
    }
    return arrivalItemRepository.findByArrivalId(arrivalId).stream()
        .map(arrivalItemMapper::toResponse)
        .toList();
  }
}
