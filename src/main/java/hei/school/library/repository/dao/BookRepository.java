package hei.school.library.repository.dao;

import hei.school.library.entity.Book;
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
  Optional<Book> findByIsbn(String isbn);

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
