package hei.school.library.repository.dao;

import hei.school.library.entity.User;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends JpaRepository<User, java.util.UUID> {

  @Query(
      value =
          """
          INSERT INTO users (last_name, first_name, birth_date, email, password, phone, role)
          VALUES (:lastName, :firstName, :birthDate, :email, :password, :phone, :role)
          ON CONFLICT (email) DO NOTHING
          RETURNING id, last_name, first_name, birth_date, email, password, phone, role, created_at, updated_at
          """,
      nativeQuery = true)
  Optional<User> create(
      @Param("lastName") String lastName,
      @Param("firstName") String firstName,
      @Param("birthDate") LocalDate birthDate,
      @Param("email") String email,
      @Param("password") String password,
      @Param("phone") String phone,
      @Param("role") String role);
}
