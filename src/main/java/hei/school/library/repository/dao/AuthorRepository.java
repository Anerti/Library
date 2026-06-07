package hei.school.library.repository.dao;

import hei.school.library.entity.Author;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, UUID> {

  @Query(
      value =
          """
          SELECT id, first_name, last_name FROM author
          WHERE (:search IS NULL OR :search = ''
             OR first_name ILIKE '%' || :search || '%'
             OR last_name  ILIKE '%' || :search || '%')
          """,
      countQuery =
          """
          SELECT COUNT(id) FROM author
          WHERE (:search IS NULL OR :search = ''
             OR first_name ILIKE '%' || :search || '%'
             OR last_name  ILIKE '%' || :search || '%')
          """,
      nativeQuery = true)
  Page<Author> findBySearch(@Param("search") String search, Pageable pageable);

  @Query(
      value =
          """
          INSERT INTO author (first_name, last_name)
          VALUES (:firstName, :lastName)
          ON CONFLICT (first_name, last_name) DO NOTHING
          RETURNING id, first_name, last_name
          """,
      nativeQuery = true)
  Optional<Author> create(
      @Param("firstName") String firstName, @Param("lastName") String lastName);
}
