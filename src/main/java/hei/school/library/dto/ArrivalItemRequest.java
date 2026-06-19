package hei.school.library.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ArrivalItemRequest {
    private UUID bookCopyId;
    private Double purchasePrice;
    private Integer quantity;
}