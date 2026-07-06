package hei.school.library.repository.dao;

import hei.school.library.entity.BookCopy;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalyticsRepository extends JpaRepository<BookCopy, UUID> {

  @Query(
      value =
          """
            SELECT COALESCE(
            (SELECT COUNT(abc.book_copy_id)
             FROM arrival_book_copy abc
                 JOIN book_copy bc ON bc.id = abc.book_copy_id
             WHERE bc.book_id = CAST(:bookId AS uuid)
               AND bc.library_id = CAST(:libraryId AS uuid)
               AND (:format IS NULL OR bc.format = CAST(:format AS book_copy_format)))
                -
            (SELECT COUNT(sbc.book_copy_id)
             FROM sale_book_copy sbc
                 JOIN book_copy bc ON bc.id = sbc.book_copy_id
                 JOIN sale s ON s.id = sbc.sale_id
             WHERE bc.book_id = CAST(:bookId AS uuid)
               AND bc.library_id = CAST(:libraryId AS uuid)
               AND (:format IS NULL OR bc.format = CAST(:format AS book_copy_format))
               AND s.status = 'SOLD'),
               0)
          """,
      nativeQuery = true)
  long countAvailableStock(
      @Param("bookId") UUID bookId,
      @Param("format") String format,
      @Param("libraryId") UUID libraryId);

  @Query(
      value =
          """
            SELECT DISTINCT(book_copy.library_id, book_copy.book_id)
            FROM book_copy
            WHERE library_id = CAST(:libraryId AS uuid)
            AND book_id = CAST(:bookId AS uuid)
          """,
      nativeQuery = true)
  Object checkBookCopyLink(@Param("libraryId") UUID libraryId, @Param("bookId") UUID bookId);

  @Query(
      value =
          """
          WITH arrival_count AS (
              SELECT book_copy_id, COUNT(book_copy_id) AS cnt
              FROM arrival_book_copy
              GROUP BY book_copy_id
          ),
          sold_count AS (
              SELECT sbc.book_copy_id, COUNT(sbc.book_copy_id) AS cnt
              FROM sale_book_copy sbc
              JOIN sale s ON s.id = sbc.sale_id AND s.status = 'SOLD'
              GROUP BY sbc.book_copy_id
          )
          SELECT
              bc.book_id,
              bc.library_id,
              bc.format,
              COALESCE(ac.cnt, 0) - COALESCE(sc.cnt, 0) AS stock
          FROM book_copy bc
          LEFT JOIN arrival_count ac ON ac.book_copy_id = bc.id
          LEFT JOIN sold_count sc ON sc.book_copy_id = bc.id
          WHERE bc.library_id = CAST(:libraryId AS uuid)
            AND bc.book_id = CAST(:bookId AS uuid)
            AND (:format IS NULL OR bc.format = CAST(:format AS book_copy_format))
          GROUP BY bc.book_id, bc.library_id, bc.format, ac.cnt, sc.cnt
          HAVING COALESCE(ac.cnt, 0) - COALESCE(sc.cnt, 0) <= :threshold
          """,
      nativeQuery = true)
  List<Object[]> findLowStockBooks(
      @Param("libraryId") UUID libraryId,
      @Param("bookId") UUID bookId,
      @Param("threshold") int threshold,
      @Param("format") String format);
}
