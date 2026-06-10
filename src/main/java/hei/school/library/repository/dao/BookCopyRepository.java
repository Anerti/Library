package hei.school.library.repository.dao;

import hei.school.library.entity.BookCopy;
import hei.school.library.entity.enums.BookCopyFormat;
import hei.school.library.entity.enums.BookCopyStatus;
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
      "SELECT bc FROM BookCopy bc WHERE bc.library.id = :libraryId "
          + "AND (:status IS NULL OR bc.status = :status) "
          + "AND (:format IS NULL OR bc.format = :format) "
          + "AND (:bookId IS NULL OR bc.book.id = :bookId)")
  Page<BookCopy> findByFilters(
      @Param("libraryId") UUID libraryId,
      @Param("status") BookCopyStatus status,
      @Param("format") BookCopyFormat format,
      @Param("bookId") UUID bookId,
      Pageable pageable);

  @Query(
      value =
          """
          INSERT INTO book_copy (id, price, format, library_id, book_id, status, page_number, updated_at)
          VALUES (gen_random_uuid(), :price, :format, :libraryId, :bookId, 'AVAILABLE', :pageNumber, now())
          RETURNING id, price, format, library_id, book_id, status, page_number, updated_at
          """,
      nativeQuery = true)
  Optional<BookCopy> create(
      @Param("price") Double price,
      @Param("format") String format,
      @Param("libraryId") UUID libraryId,
      @Param("bookId") UUID bookId,
      @Param("pageNumber") Integer pageNumber);
}
