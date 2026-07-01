package hei.school.library.repository.dao;

import hei.school.library.entity.Genre;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GenreRepository extends JpaRepository<Genre, UUID> {
  @Query(
      value =
          """
          INSERT INTO genre (id, name)
          VALUES (gen_random_uuid(), :name)
          ON CONFLICT (name) DO NOTHING
          returning id, name
          """,
      nativeQuery = true)
  Optional<Genre> insertGenreIgnoreConflict(@Param("name") String name);

  @Query(
      value =
          """
          DELETE FROM genre WHERE id = :id
          RETURNING id
          """,
      nativeQuery = true)
  Optional<UUID> deleteByUUId(@Param("id") UUID id);

  @Query(
      value =
          """
          UPDATE genre
          SET name = :name
          WHERE id = :id
          RETURNING id, name
          """,
      nativeQuery = true)
  Optional<Genre> updateGenreName(@Param("id") UUID id, @Param("name") String name);

  @Query(
      value =
          """
          SELECT id, name FROM genre
          WHERE (:search IS NULL OR :search = ''
             OR name ILIKE '%' || :search || '%')
          """,
      countQuery =
          """
          SELECT COUNT(id) FROM genre
          WHERE (:search IS NULL OR :search = ''
             OR name ILIKE '%' || :search || '%')
          """,
      nativeQuery = true)
  Page<Genre> findBySearch(@Param("search") String search, Pageable pageable);
}
