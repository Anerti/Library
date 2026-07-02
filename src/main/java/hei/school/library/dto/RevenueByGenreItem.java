package hei.school.library.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RevenueByGenreItem {
  private UUID genreId;
  private String genreName;
  private BigDecimal totalRevenue;
  private Integer totalSold;
}
