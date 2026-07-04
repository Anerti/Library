package hei.school.library.dto;

import hei.school.library.entity.enums.SaleStatus;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaleResponse {
  private UUID id;
  private Instant saleDate;
  private UserResponse user;
  private SaleStatus status;
  private LibraryResponse library;
  private Instant createdAt;
}
