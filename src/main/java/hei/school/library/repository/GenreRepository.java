package hei.school.library.repository;

import hei.school.library.entity.GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface GenreRepository extends JpaRepository<GenreEntity, UUID> {
    boolean existsByNameIgnoreCase(String name);
}
