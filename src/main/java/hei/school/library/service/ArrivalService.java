package hei.school.library.service;

import hei.school.library.dto.ArrivalResponse;
import hei.school.library.dto.PageResponse;
import hei.school.library.dto.PaginationDto;
import hei.school.library.entity.Arrival;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.ArrivalMapper;
import hei.school.library.repository.dao.ArrivalRepository;
import hei.school.library.repository.dao.LibraryRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ArrivalService {
  private final ArrivalRepository arrivalRepository;
  private final LibraryRepository libraryRepository;
  private final ArrivalMapper arrivalMapper;

  @Transactional(readOnly = true)
  public PageResponse<ArrivalResponse> findByLibraryId(
      UUID libraryId, LocalDateTime from, LocalDateTime to, int page, int size) {
    if (!libraryRepository.existsById(libraryId)) {
      throw new NotFoundException("Library with id " + libraryId + " not found");
    }

    Pageable pageable = PageRequest.of(page - 1, size);

    Page<Arrival> result =
        arrivalRepository.findByLibraryIdAndDateRange(libraryId, from, to, pageable);

    return PageResponse.<ArrivalResponse>builder()
        .data(result.getContent().stream().map(arrivalMapper::toResponse).toList())
        .pagination(
            PaginationDto.builder().page(page).size(size).total(result.getTotalElements()).build())
        .build();
  }

  public ArrivalResponse findById(UUID libraryId, UUID arrivalId) {
    if (!libraryRepository.existsById(libraryId)) {
      throw new NotFoundException("Library with id " + libraryId + " not found");
    }
    return arrivalRepository
        .findById(arrivalId)
        .map(arrivalMapper::toResponse)
        .orElseThrow(() -> new NotFoundException("Arrival with id " + arrivalId + " not found"));
  }
}
