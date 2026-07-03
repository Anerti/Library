package hei.school.library.service;

import hei.school.library.dto.SaleBookCopyRequest;
import hei.school.library.dto.SaleBookCopyResponse;
import hei.school.library.exception.ConflictException;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.SaleBookCopyMapper;
import hei.school.library.repository.dao.SaleBookCopyRepository;
import hei.school.library.repository.dao.SaleRepository;
import hei.school.library.validator.SaleBookCopyValidator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SaleBookCopyService {

  private final SaleBookCopyRepository saleBookCopyRepository;
  private final SaleRepository saleRepository;
  private final SaleBookCopyMapper saleBookCopyMapper;
  private final SaleBookCopyValidator saleBookCopyValidator;

  @Transactional(readOnly = true)
  public List<SaleBookCopyResponse> findBySaleId(UUID saleId) {
    saleRepository
        .findById(saleId)
        .orElseThrow(() -> new NotFoundException("Sale " + saleId + " not found"));

    return saleBookCopyRepository.findBySaleId(saleId).stream()
        .map(saleBookCopyMapper::toResponse)
        .toList();
  }

  @Transactional
  public SaleBookCopyResponse create(UUID saleId, SaleBookCopyRequest request) {
    saleBookCopyValidator.validateCreate(request);

    saleRepository
        .findById(saleId)
        .orElseThrow(() -> new NotFoundException("Sale " + saleId + " not found"));

    Integer quantity = request.getQuantity() != null ? request.getQuantity() : 1;

    return saleBookCopyMapper.toResponse(
        saleBookCopyRepository
            .create(request.getBookCopyId(), saleId, quantity, request.getPrice())
            .orElseThrow(
                () ->
                    new ConflictException(
                        "SaleBookCopy with bookCopyId "
                            + request.getBookCopyId()
                            + " already exists in sale "
                            + saleId)));
  }

  @Transactional
  public void delete(UUID saleId, UUID bookCopyId) {
    saleRepository
        .findById(saleId)
        .orElseThrow(() -> new NotFoundException("Sale " + saleId + " not found"));

    saleBookCopyRepository
        .delete(saleId, bookCopyId)
        .orElseThrow(
            () ->
                new NotFoundException(
                    "SaleBookCopy with bookCopyId " + bookCopyId + " not found in sale " + saleId));
  }
}
