package hei.school.library.mapper;

import hei.school.library.dto.BookLowStockResponse;
import hei.school.library.dto.BookStockResponse;
import hei.school.library.dto.GenreSummary;
import hei.school.library.dto.PaginationDto;
import hei.school.library.dto.RevenueByGenreItem;
import hei.school.library.dto.RevenueByGenreResponse;
import hei.school.library.entity.enums.BookCopyFormat;
import hei.school.library.projection.RevenueByGenreProjection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsMapper {

  public BookStockResponse toStockResponse(UUID bookId, long total, BookCopyFormat byFormat) {
    return BookStockResponse.builder().bookId(bookId).total(total).byFormat(byFormat).build();
  }

  public BookLowStockResponse toLowStockResponse(Object[] row) {
    return BookLowStockResponse.builder()
        .bookId(UUID.fromString(row[0].toString()))
        .libraryId(UUID.fromString(row[1].toString()))
        .format(row[2] != null ? BookCopyFormat.valueOf(row[2].toString()) : null)
        .stock(((Number) row[3]).longValue())
        .build();
  }

  public RevenueByGenreResponse toRevenueByGenreResponse(
      Page<RevenueByGenreProjection> found, int page, int size) {
    List<RevenueByGenreItem> items =
        found.getContent().stream()
            .map(
                p ->
                    new RevenueByGenreItem(
                        new GenreSummary(p.getGenreId(), p.getGenreName()),
                        p.getTotalRevenue(),
                        p.getTotalSold()))
            .toList();

    return RevenueByGenreResponse.builder()
        .data(items.isEmpty() ? null : items)
        .meta(new PaginationDto(page, size, found.getTotalElements()))
        .build();
  }
}
