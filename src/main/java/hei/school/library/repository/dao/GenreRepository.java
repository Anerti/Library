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
          INSERT INTO genre (id, name)
          VALUES (gen_random_uuid(), :name)
          ON CONFLICT (name) DO NOTHING
          returning id, name, created_at, updated_at
          """,
            nativeQuery = true)
    Optional<Genre> insertGenreIgnoreConflict(
            @Param("name") String name
    );
}
