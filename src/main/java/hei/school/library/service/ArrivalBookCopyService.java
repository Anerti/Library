package hei.school.library.service;

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
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArrivalBookCopyService {
  private final ArrivalBookCopyRepository arrivalBookCopyRepository;
  private final ArrivalRepository arrivalRepository;
  private final BookCopyRepository bookCopyRepository;
  private final ArrivalBookCopyMapper arrivalBookCopyMapper;

  public List<ArrivalBookCopyResponse> findByArrivalId(UUID arrivalId) {
    if (!arrivalRepository.existsById(arrivalId)) {
      throw new NotFoundException("Arrival with id " + arrivalId + " not found");
    }
    return arrivalBookCopyRepository.findByArrivalId(arrivalId).stream()
        .map(arrivalBookCopyMapper::toResponse)
        .toList();
  }

  public ArrivalBookCopyResponse create(UUID arrivalId, ArrivalBookCopyRequest request) {
    Arrival arrival =
        arrivalRepository
            .findById(arrivalId)
            .orElseThrow(
                () -> new NotFoundException("Arrival with id " + arrivalId + " not found"));

    BookCopy bookCopy =
        bookCopyRepository
            .findById(request.getBookCopyId())
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "BookCopy with id " + request.getBookCopyId() + " not found"));

    ArrivalBookCopy item =
        ArrivalBookCopy.builder()
            .arrival(arrival)
            .bookCopy(bookCopy)
            .purchasePrice(request.getPurchasePrice())
            .build();

    return arrivalBookCopyMapper.toResponse(arrivalBookCopyRepository.save(item));
  }

  public void delete(UUID arrivalId, UUID bookCopyId) {
    ArrivalBookCopy item =
        arrivalBookCopyRepository
            .findByArrivalIdAndBookCopyId(arrivalId, bookCopyId)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "ArrivalBookCopy not found for arrival "
                            + arrivalId
                            + " and bookCopy "
                            + bookCopyId));

    arrivalBookCopyRepository.delete(item);
  }
}
