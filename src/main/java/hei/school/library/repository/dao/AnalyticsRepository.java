package hei.school.library.repository.dao;

import hei.school.library.entity.BookCopy;
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
}
