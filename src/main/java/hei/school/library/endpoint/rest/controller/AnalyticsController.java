package hei.school.library.endpoint.rest.controller;

import hei.school.library.dto.BookStockResponse;
import hei.school.library.service.AnalyticsService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AnalyticsController {

  private final AnalyticsService analyticsService;

  @GetMapping("/libraries/{libraryId}/analytics/stock/{bookCopyId}")
  public ResponseEntity<BookStockResponse> getStockOverview(
      @PathVariable UUID libraryId,
      @PathVariable UUID bookCopyId,
      @RequestParam(defaultValue = "ALL") String format) {
    return ResponseEntity.status(HttpStatus.OK).body(analyticsService.getStockOverview(libraryId, bookCopyId, format));
  }
}
