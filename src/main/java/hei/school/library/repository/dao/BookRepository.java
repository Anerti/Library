package hei.school.library.repository.dao;

import hei.school.library.entity.Book;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {

  @Query(
      value =
          """
          INSERT INTO book (title, summary, isbn, publisher, published_at)
          VALUES (:title, :summary, :isbn, :publisher, :publishedAt)
          ON CONFLICT (isbn) DO NOTHING
          RETURNING id, title, summary, isbn, publisher, published_at, created_at
          """,
      nativeQuery = true)
  Optional<Book> create(
      @Param("title") String title,
      @Param("summary") String summary,
      @Param("isbn") String isbn,
      @Param("publisher") String publisher,
      @Param("publishedAt") LocalDate publishedAt);

  @Query(
      value =
          """
          DELETE FROM book WHERE id = :id
          RETURNING id
          """,
      nativeQuery = true)
  Optional<UUID> deleteByIdAndReturn(@Param("id") UUID id);

  @Query(
      """
      SELECT b FROM Book b
      LEFT JOIN b.authors a
      WHERE (:search IS NULL OR :search = ''
          OR LOWER(b.title) LIKE LOWER(CONCAT('%', :search, '%'))
          OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :search, '%'))
          OR LOWER(b.publisher) LIKE LOWER(CONCAT('%', :search, '%'))
          OR LOWER(a.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
      )
      AND (:isbn IS NULL OR b.isbn = :isbn)
      AND (:authorId IS NULL OR a.id = :authorId)
      """)
  Page<Book> searchBooks(
      @Param("search") String search,
      @Param("isbn") String isbn,
      @Param("authorId") UUID authorId,
      Pageable pageable);
}
