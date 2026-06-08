package hei.school.library.repository.dao;

import hei.school.library.entity.Book;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {
  Optional<Book> findByIsbn(String isbn);
}
