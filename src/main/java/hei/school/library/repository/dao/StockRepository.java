package hei.school.library.repository.dao;

import hei.school.library.entity.BookCopy;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<BookCopy, UUID> {

  @Query(
      value =
          """
          SELECT
              COALESCE(SUM(ai.quantity), 0) - COALESCE(SUM(si.quantity), 0)
          FROM book_copy bc
          LEFT JOIN arrival_item ai ON ai.book_copy_id = bc.id
          LEFT JOIN sale_book_copy si ON si.book_copy_id = bc.id
          WHERE bc.book_id = :bookId
          AND (:format IS NULL OR bc.format = CAST(:format AS VARCHAR))
          AND (:libraryId IS NULL OR bc.library_id = CAST(:libraryId AS uuid))
          GROUP BY bc.id
          """,
      nativeQuery = true)
  List<Long> calculateStock(
      @Param("bookId") UUID bookId,
      @Param("format") String format,
      @Param("libraryId") String libraryId);
}
