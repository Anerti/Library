package hei.school.library.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RevenueByGenreItem {
  private GenreSummary genre;
  private BigDecimal totalRevenue;
  private Integer totalSold;
}
