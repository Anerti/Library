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
          UPDATE book
          SET title       = :title,
              summary     = :summary,
              isbn        = :isbn,
              publisher   = :publisher,
              published_at = :publishedAt
          WHERE id = :id
          RETURNING id, title, summary, isbn, publisher, published_at, created_at
          """,
      nativeQuery = true)
  Optional<Book> updateById(
      @Param("id") UUID id,
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
      value =
          """
          SELECT DISTINCT b.id, b.title, b.summary, b.isbn, b.publisher, b.published_at, b.created_at FROM book b
          LEFT JOIN author_book ab ON ab.book_id = b.id
          LEFT JOIN author a ON a.id = ab.author_id
          LEFT JOIN book_genre bg ON bg.book_id = b.id
          LEFT JOIN genre g ON g.id = bg.genre_id
          WHERE (:search IS NULL OR :search = ''
              OR b.title ILIKE '%' || :search || '%'
              OR b.isbn ILIKE '%' || :search || '%'
              OR b.publisher ILIKE '%' || :search || '%'
              OR a.last_name ILIKE '%' || :search || '%'
          )
          AND (:isbn IS NULL OR b.isbn = :isbn)
          AND (:authorLastName IS NULL OR a.last_name = :authorLastName)
          AND (:genreName IS NULL OR g.name = :genreName)
          """,
      countQuery =
          """
          SELECT COUNT(DISTINCT b.id) FROM book b
          LEFT JOIN author_book ab ON ab.book_id = b.id
          LEFT JOIN author a ON a.id = ab.author_id
          LEFT JOIN book_genre bg ON bg.book_id = b.id
          LEFT JOIN genre g ON g.id = bg.genre_id
          WHERE (:search IS NULL OR :search = ''
              OR b.title ILIKE '%' || :search || '%'
              OR b.isbn ILIKE '%' || :search || '%'
              OR b.publisher ILIKE '%' || :search || '%'
              OR a.last_name ILIKE '%' || :search || '%'
          )
          AND (:isbn IS NULL OR b.isbn = :isbn)
          AND (:authorLastName IS NULL OR a.last_name = :authorLastName)
          AND (:genreName IS NULL OR g.name = :genreName)
          """,
      nativeQuery = true)
  Page<Book> searchBooks(
      @Param("search") String search,
      @Param("isbn") String isbn,
      @Param("authorLastName") String authorLastName,
      @Param("genreName") String genreName,
      Pageable pageable);
}
