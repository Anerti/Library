package hei.school.library.repository.dao;

import hei.school.library.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface GenreRepository extends JpaRepository<Genre, UUID> {
    @Query(
            value = """
          INSERT INTO genre (id, name, created_at, updated_at)
          VALUES (gen_random_uuid(), :name, NOW(), NOW())
          ON CONFLICT (name) DO NOTHING
          RETURNING *
          """,
            nativeQuery = true)
    Optional<Genre> insertGenreIgnoreConflict(
            @Param("name") String name
    );
}
