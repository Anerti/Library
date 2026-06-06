package hei.school.library.repository.dao;

import hei.school.library.entity.Library;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LibraryRepository extends JpaRepository<Library, UUID> {

  @Query(
      value =
          """
          SELECT id, name, phone, email, address FROM library
          WHERE (:search IS NULL OR :search = ''
            OR name ILIKE '%' || :search || '%'
            OR email ILIKE '%' || :search || '%'
            OR address ILIKE '%' || :search || '%')
          """,
      nativeQuery = true)
  Optional<Page<Library>> searchLibraries(@Param("search") String search, Pageable pageable);
}
