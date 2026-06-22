package hei.school.library.service;

import hei.school.library.dto.SaleItemResponse;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.SaleItemMapper;
import hei.school.library.repository.dao.SaleItemRepository;
import hei.school.library.repository.dao.SaleRepository;
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

  @Transactional(readOnly = true)
  public List<SaleItemResponse> findBySaleId(UUID saleId) {
    saleRepository
        .findById(saleId)
        .orElseThrow(() -> new NotFoundException("Sale " + saleId + " not found"));

    return saleItemRepository.findBySaleId(saleId).stream()
        .map(saleItemMapper::toResponse)
        .toList();
  }
}
