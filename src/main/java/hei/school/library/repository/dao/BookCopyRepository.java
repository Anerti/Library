package hei.school.library.repository.dao;

import hei.school.library.entity.BookCopy;
import hei.school.library.entity.enums.BookCopyFormat;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, UUID> {
  @Query(
      value =
          """
          SELECT bc.id, bc.book_id, bc.library_id, bc.format,
                 bc.status, bc.price, bc.page_number, bc.updated_at
          FROM book_copy bc
          WHERE bc.library_id = CAST(:libraryId AS uuid)
          AND (:status IS NULL OR bc.status = CAST(:status AS book_copy_status))
          AND (:format IS NULL OR bc.format = CAST(:format AS book_copy_format))
          AND (CAST(:bookId AS uuid) IS NULL OR bc.book_id = CAST(:bookId AS uuid))
          """,
      countQuery =
          """
          SELECT COUNT(id) FROM book_copy bc
          WHERE bc.library_id = CAST(:libraryId AS uuid)
          AND (:status IS NULL OR bc.status = CAST(:status AS book_copy_status))
          AND (:format IS NULL OR bc.format = CAST(:format AS book_copy_format))
          AND (CAST(:bookId AS uuid) IS NULL OR bc.book_id = CAST(:bookId AS uuid))
          """,
      nativeQuery = true)
  Page<BookCopy> findByFilters(
      @Param("libraryId") UUID libraryId,
      @Param("status") String status,
      @Param("format") String format,
      @Param("bookId") String bookId,
      Pageable pageable);

  @Query(
      value =
          """
          INSERT INTO book_copy (id, price, format, library_id, book_id, status, page_number, updated_at)
          VALUES (gen_random_uuid(), :price, CAST(:format AS book_copy_format), :libraryId, :bookId, 'AVAILABLE', :pageNumber, now())
          RETURNING id, price, format, library_id, book_id, status, page_number, updated_at
          """,
      nativeQuery = true)
  Optional<BookCopy> create(
      @Param("price") Double price,
      @Param("format") String format,
      @Param("libraryId") UUID libraryId,
      @Param("bookId") UUID bookId,
      @Param("pageNumber") Integer pageNumber);

  @Query(
      """
      SELECT COUNT(bc) FROM BookCopy bc
      WHERE bc.book.id = :bookId
      AND bc.status = 'AVAILABLE'
      AND (:format IS NULL OR bc.format = :format)
      AND (:libraryId IS NULL OR bc.library.id = :libraryId)
      """)
  long countAvailableStock(
      @Param("bookId") UUID bookId,
      @Param("format") BookCopyFormat format,
      @Param("libraryId") UUID libraryId);
}
