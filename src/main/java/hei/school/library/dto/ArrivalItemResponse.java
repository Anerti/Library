package hei.school.library.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArrivalItemResponse {
    private UUID bookCopyId;
    private UUID arrivalId;
    private LocalDateTime createdAt;
    private Double purchasePrice;
    private Integer quantity;
    private LocalDateTime updatedAt;
}