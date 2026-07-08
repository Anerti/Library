package hei.school.library.projection;

import java.math.BigDecimal;
import java.util.UUID;

public interface RevenueByGenreProjection {
  UUID getGenreId();

  String getGenreName();

  BigDecimal getTotalRevenue();

  Integer getTotalSold();
}
