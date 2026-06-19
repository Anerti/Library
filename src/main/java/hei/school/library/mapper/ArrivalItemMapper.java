package hei.school.library.mapper;

import hei.school.library.dto.ArrivalItemResponse;
import hei.school.library.entity.ArrivalItem;
import org.springframework.stereotype.Component;

@Component
public class ArrivalItemMapper {
    public ArrivalItemResponse toResponse(ArrivalItem item) {
        return ArrivalItemResponse.builder()
                .bookCopyId(item.getBookCopy().getId())
                .arrivalId(item.getArrival().getId())
                .createdAt(item.getCreatedAt())
                .purchasePrice(item.getPurchasePrice())
                .quantity(item.getQuantity())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}