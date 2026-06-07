package hei.school.library.repository.dao;

import hei.school.library.entity.Author;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, UUID> {

  Boolean existsByFirstNameAndLastName(String firstName, String lastName);

  @Query(
      value =
          """
          SELECT * FROM author
          WHERE (:search IS NULL OR :search = ''
             OR first_name ILIKE '%' || :search || '%'
             OR last_name  ILIKE '%' || :search || '%')
          """,
      countQuery =
          """
          SELECT COUNT(*) FROM author
          WHERE (:search IS NULL OR :search = ''
             OR first_name ILIKE '%' || :search || '%'
             OR last_name  ILIKE '%' || :search || '%')
          """,
      nativeQuery = true)
  Page<Author> findBySearch(@Param("search") String search, Pageable pageable);
}
