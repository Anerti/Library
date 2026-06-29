package hei.school.library.repository.dao;

import hei.school.library.entity.User;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

  @Query(
      value =
"""
SELECT id, last_name, first_name, birth_date, email, password, phone, role, created_at, updated_at FROM users
WHERE (:search IS NULL OR :search = ''
   OR last_name  ILIKE '%' || :search || '%'
   OR first_name ILIKE '%' || :search || '%'
   OR email      ILIKE '%' || :search || '%')
""",
      countQuery =
          """
          SELECT COUNT(id) FROM users
          WHERE (:search IS NULL OR :search = ''
             OR last_name  ILIKE '%' || :search || '%'
             OR first_name ILIKE '%' || :search || '%'
             OR email      ILIKE '%' || :search || '%')
          """,
      nativeQuery = true)
  Page<User> findBySearch(@Param("search") String search, Pageable pageable);

  @Query(
      value =
          """
          UPDATE users
          SET
            last_name = COALESCE(:lastName, last_name),
            first_name = COALESCE(:firstName, first_name),
            birth_date = COALESCE(:birthDate, birth_date),
            email = COALESCE(:email, email),
            phone = COALESCE(:phone, phone),
            updated_at = NOW()
          WHERE id = :id
          RETURNING id, last_name, first_name, birth_date, email, phone, role, created_at, updated_at
          """,
      nativeQuery = true)
  Optional<User> patch(
      @Param("id") UUID id,
      @Param("lastName") String lastName,
      @Param("firstName") String firstName,
      @Param("birthDate") LocalDate birthDate,
      @Param("email") String email,
      @Param("phone") String phone);

  @Query(value = "SELECT id, last_name, first_name, birth_date, email, password, phone, role FROM users WHERE email = :email", nativeQuery = true)
  Optional<User> findByEmail(@Param("email") String email);

  @Query(
      value =
          """
          DELETE FROM users WHERE id = :id
          RETURNING id
          """,
      nativeQuery = true)
  Optional<UUID> delete(@Param("id") UUID id);
}
