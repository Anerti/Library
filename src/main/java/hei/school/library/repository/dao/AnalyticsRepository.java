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
          WITH book_stock AS (
              SELECT
                  bc.book_id,
                  bc.library_id,
                  bc.format,
                  (SELECT COUNT(abc.book_copy_id)
                   FROM arrival_book_copy abc
                   JOIN book_copy bc2 ON bc2.id = abc.book_copy_id
                   WHERE bc2.book_id = bc.book_id
                     AND bc2.library_id = bc.library_id
                     AND bc2.format = bc.format)
                  -
                  (SELECT COUNT(sbc.book_copy_id)
                   FROM sale_book_copy sbc
                   JOIN book_copy bc3 ON bc3.id = sbc.book_copy_id
                   JOIN sale s ON s.id = sbc.sale_id
                   WHERE bc3.book_id = bc.book_id
                     AND bc3.library_id = bc.library_id
                     AND bc3.format = bc.format
                     AND s.status = 'SOLD') AS stock
              FROM book_copy bc
              WHERE bc.library_id = CAST(:libraryId AS uuid)
              GROUP BY bc.book_id, bc.library_id, bc.format
          )
          SELECT book_id, library_id, format, stock
          FROM book_stock
          WHERE stock <= :threshold
          """,
      nativeQuery = true)
  List<Object[]> findLowStockBooks(
      @Param("libraryId") UUID libraryId, @Param("threshold") int threshold);
}
