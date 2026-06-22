package hei.school.library.service;

import hei.school.library.dto.SaleItemRequest;
import hei.school.library.dto.SaleItemResponse;
import hei.school.library.exception.ConflictException;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.SaleItemMapper;
import hei.school.library.repository.dao.SaleItemRepository;
import hei.school.library.repository.dao.SaleRepository;
import hei.school.library.validator.SaleItemValidator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SaleItemService {

  private final SaleItemRepository saleItemRepository;
  private final SaleRepository saleRepository;
  private final SaleItemMapper saleItemMapper;
  private final SaleItemValidator saleItemValidator;

  @Transactional(readOnly = true)
  public List<SaleItemResponse> findBySaleId(UUID saleId) {
    saleRepository
        .findById(saleId)
        .orElseThrow(() -> new NotFoundException("Sale " + saleId + " not found"));

    return saleItemRepository.findBySaleId(saleId).stream()
        .map(saleItemMapper::toResponse)
        .toList();
  }

  @Transactional
  public SaleItemResponse create(UUID saleId, SaleItemRequest request) {
    saleItemValidator.validateCreate(request);

    saleRepository
        .findById(saleId)
        .orElseThrow(() -> new NotFoundException("Sale " + saleId + " not found"));

    Integer quantity = request.getQuantity() != null ? request.getQuantity() : 1;

    return saleItemMapper.toResponse(
        saleItemRepository
            .create(request.getBookCopyId(), saleId, quantity, request.getPrice())
            .orElseThrow(
                () ->
                    new ConflictException(
                        "SaleItem with bookCopyId "
                            + request.getBookCopyId()
                            + " already exists in sale "
                            + saleId)));
  }

  @Transactional
  public void delete(UUID saleId, UUID bookCopyId) {
    saleRepository
        .findById(saleId)
        .orElseThrow(() -> new NotFoundException("Sale " + saleId + " not found"));

    saleItemRepository
        .delete(saleId, bookCopyId)
        .orElseThrow(
            () ->
                new NotFoundException(
                    "SaleItem with bookCopyId " + bookCopyId + " not found in sale " + saleId));
  }
}
