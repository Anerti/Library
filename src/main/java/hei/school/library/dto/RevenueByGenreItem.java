package hei.school.library.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;
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
