package hei.school.library.repository.dao;

import hei.school.library.entity.Library;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
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

  @Query(
      value =
          """
          INSERT INTO library (id, name, phone, email, address)
          VALUES (gen_random_uuid(), :name, :phone, :email, :address)
          ON CONFLICT (email) DO NOTHING
          returning id, name, phone, email, address
          """,
      nativeQuery = true)
  Optional<Library> insertLibraryIgnoreConflict(
      @Param("name") String name,
      @Param("phone") String phone,
      @Param("email") String email,
      @Param("address") String address);

  @Query(
      value =
          """
          UPDATE library
          SET
            name = COALESCE(:name, name),
            phone = COALESCE(:phone, phone),
            email = COALESCE(:email, email),
            address = COALESCE(:address, address)
          WHERE id = :id
          RETURNING id, name, phone, email, address
          """,
      nativeQuery = true)
  Optional<Library> patch(
      @Param("id") UUID id,
      @Param("name") String name,
      @Param("phone") String phone,
      @Param("email") String email,
      @Param("address") String address);
}
