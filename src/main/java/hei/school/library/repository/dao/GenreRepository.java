package hei.school.library.repository.dao;

import hei.school.library.entity.Genre;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GenreRepository extends JpaRepository<Genre, UUID> {
  @Query(
      value =
          """
          INSERT INTO genre (id, name, created_at, updated_at)
          VALUES (gen_random_uuid(), :name, NOW(), NOW())
          ON CONFLICT (name) DO NOTHING
          returning id, name, created_at, updated_at
          """,
      nativeQuery = true)
  Optional<Genre> insertGenreIgnoreConflict(@Param("name") String name);

    @Query(
            value =
                    """
                    UPDATE genre 
                    SET name = :name, updated_at = NOW() 
                    WHERE id = :id
                    RETURNING id, name, created_at, updated_at
                    """,
            nativeQuery = true)
    Optional<Genre> updateGenreName(@Param("id") UUID id, @Param("name") String name);

}
